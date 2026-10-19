package com.uniquindio.segundaMano.domain.entity;

import com.uniquindio.segundaMano.domain.valueObject.VigenciaAcademica;
import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import com.uniquindio.segundaMano.domain.valueObject.EstadoOferta;
import com.uniquindio.segundaMano.domain.valueObject.EstadoPublicacion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Publicacion {

    /** Regla 7: una Publicación expira a los 60 días sin actividad (tenga o no VigenciaAcademica). */
    public static final int DIAS_MAXIMOS_SIN_ACTIVIDAD = 60;

    private final String codigo;
    private final UniCambista dueno;
    private String titulo;
    private BigDecimal valor;
    private EstadoPublicacion estado;
    private VigenciaAcademica vigenciaAcademica; // puede quedar en null
    private LocalDate fechaUltimaActividad;
    private final List<Oferta> ofertas = new ArrayList<>();

    private Publicacion(UniCambista dueno, String titulo, BigDecimal valor) {
        this.codigo = UUID.randomUUID().toString();
        this.dueno = dueno;
        this.titulo = titulo;
        this.valor = valor;
        this.estado = EstadoPublicacion.ACTIVA;
        this.fechaUltimaActividad = LocalDate.now();
    }

    public static Publicacion publicar(UniCambista dueno, String titulo, BigDecimal valor) {
        if (dueno == null) {
            throw new ReglaDominioException("La Publicación debe tener un dueño");
        }
        if (!dueno.puedeCrearPublicacion()) {
            throw new ReglaDominioException("El UniCambista debe verificar su correo antes de publicar");
        }
        validarTitulo(titulo);
        validarValor(valor);
        return new Publicacion(dueno, titulo.trim(), valor);
    }

    /**
     * Regla 2: mientras la Publicación está en proceso de compra (o eliminada / expirada)
     * su título y su valor no pueden modificarse. Solo el dueño la edita.
     */
    public void editar(UniCambista quienEdita, String nuevoTitulo, BigDecimal nuevoValor) {
        validarDueno(quienEdita);
        validarActiva("Solo se puede modificar una Publicación activa: en proceso de compra no se puede cambiar");
        validarTitulo(nuevoTitulo);
        validarValor(nuevoValor);
        this.titulo = nuevoTitulo.trim();
        this.valor = nuevoValor;
        registrarActividad();
    }

    /** Solo la propia raíz pasa la Publicación a EN_PROCESO, al aceptarse una Oferta. */
    private void marcarEnProceso() {
        this.estado = EstadoPublicacion.EN_PROCESO;
    }

    /**
     * Regla 4: una Publicación nunca se borra físicamente; solo su dueño la marca como ELIMINADA.
     * Si tiene una Transacción activa, la Transacción se conserva por trazabilidad.
     */
    public void eliminarLogicamente(UniCambista quienElimina) {
        validarDueno(quienElimina);
        if (this.estado == EstadoPublicacion.ELIMINADA) {
            throw new ReglaDominioException("La Publicación ya está eliminada");
        }
        this.estado = EstadoPublicacion.ELIMINADA;
    }

    /**
     * Regla 7: si la Publicación sigue ACTIVA y lleva 60 días o más sin actividad, pasa a EXPIRADA.
     * Se recibe la fecha de hoy como parámetro para que la regla sea fácil de probar.
     *
     * @return true si la Publicación expiró en esta llamada
     */
    public boolean expirarSiCorresponde(LocalDate hoy) {
        if (hoy == null) {
            throw new ReglaDominioException("Debe indicarse la fecha para evaluar la expiración");
        }
        if (this.estado != EstadoPublicacion.ACTIVA) {
            return false;
        }
        long diasSinActividad = ChronoUnit.DAYS.between(fechaUltimaActividad, hoy);
        if (diasSinActividad >= DIAS_MAXIMOS_SIN_ACTIVIDAD) {
            this.estado = EstadoPublicacion.EXPIRADA;
            return true;
        }
        return false;
    }

    // ---- Único punto de entrada al Agregado para gestionar Ofertas ----

    public Oferta recibirOferta(UniCambista oferente, BigDecimal monto) {
        validarActiva("Solo se puede ofertar sobre una publicación activa");
        Oferta nueva = Oferta.crear(this, oferente, monto);
        this.ofertas.add(nueva);
        registrarActividad();
        return nueva;
    }

    public void aceptarOferta(String idOferta, UniCambista dueno) {
        validarActiva("Solo una publicación activa puede pasar a proceso de compra");
        Oferta oferta = buscarOferta(idOferta);
        oferta.aceptar(dueno);
        cerrarNegociacionCon(oferta);
    }

    public void rechazarOferta(String idOferta, UniCambista dueno) {
        validarActiva("Solo se pueden rechazar Ofertas de una publicación activa");
        Oferta oferta = buscarOferta(idOferta);
        oferta.rechazar(dueno);
        registrarActividad();
    }

    public void contraofertar(String idOferta, UniCambista dueno, BigDecimal nuevoMonto) {
        validarActiva("Solo se puede contraofertar sobre una publicación activa");
        Oferta oferta = buscarOferta(idOferta);
        oferta.contraofertar(dueno, nuevoMonto);
        registrarActividad();
    }

    public void aceptarContraoferta(String idOferta, UniCambista oferente) {
        validarActiva("Solo una publicación activa puede pasar a proceso de compra");
        Oferta oferta = buscarOferta(idOferta);
        oferta.aceptarContraoferta(oferente);
        cerrarNegociacionCon(oferta);
    }

    /**
     * Al aceptarse una Oferta (o contraoferta) la Publicación pasa a EN_PROCESO
     * y las demás Ofertas que seguían abiertas quedan RECHAZADAS: ya no pueden prosperar.
     */
    private void cerrarNegociacionCon(Oferta aceptada) {
        for (Oferta otra : ofertas) {
            boolean abierta = otra.getEstado() == EstadoOferta.PENDIENTE
                    || otra.getEstado() == EstadoOferta.CONTRAOFERTADA;
            if (!otra.equals(aceptada) && abierta) {
                otra.descartar();
            }
        }
        marcarEnProceso();
        registrarActividad();
    }

    private void registrarActividad() {
        this.fechaUltimaActividad = LocalDate.now();
    }

    private void validarActiva(String mensaje) {
        if (this.estado != EstadoPublicacion.ACTIVA) {
            throw new ReglaDominioException(mensaje);
        }
    }

    private void validarDueno(UniCambista quien) {
        if (quien == null || !quien.equals(this.dueno)) {
            throw new ReglaDominioException("Solo el dueño puede realizar esta acción sobre la Publicación");
        }
    }

    private static void validarTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new ReglaDominioException("La Publicación debe tener un título");
        }
    }

    private static void validarValor(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) {
            throw new ReglaDominioException("El valor de la Publicación debe ser mayor a cero");
        }
    }

    private Oferta buscarOferta(String idOferta) {
        return ofertas.stream()
                .filter(o -> o.getId().equals(idOferta))
                .findFirst()
                .orElseThrow(() -> new ReglaDominioException("La Oferta no pertenece a esta Publicación"));
    }

    public List<Oferta> getOfertas() {
        return Collections.unmodifiableList(ofertas);
    }

    // ---- VigenciaAcademica ----

    public void asociarVigenciaAcademica(VigenciaAcademica vigenciaAcademica) {
        this.vigenciaAcademica = vigenciaAcademica;
    }

    public String obtenerEtiquetaVisibilidad() {
        return (vigenciaAcademica != null) ? vigenciaAcademica.toString() : null;
    }

    // ---- Getters ----

    public String getCodigo() {
        return codigo;
    }

    public UniCambista getDueno() {
        return dueno;
    }

    public String getTitulo() {
        return titulo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public EstadoPublicacion getEstado() {
        return estado;
    }

    public VigenciaAcademica getVigenciaAcademica() {
        return vigenciaAcademica;
    }

    public LocalDate getFechaUltimaActividad() {
        return fechaUltimaActividad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Publicacion otra)) return false;
        return Objects.equals(codigo, otra.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}