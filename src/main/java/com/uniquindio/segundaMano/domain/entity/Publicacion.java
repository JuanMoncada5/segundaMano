package com.uniquindio.segundaMano.domain.entity;

import com.uniquindio.segundaMano.domain.valueObject.VigenciaAcademica;
import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import com.uniquindio.segundaMano.domain.valueObject.EstadoPublicacion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Publicacion {

    private final String codigo;
    private final UniCambista dueno;
    private String titulo;
    private BigDecimal valor;
    private EstadoPublicacion estado;
    private VigenciaAcademica vigenciaAcademica; // puede quedar en null
    private final List<Oferta> ofertas = new ArrayList<>();

    private Publicacion(UniCambista dueno, String titulo, BigDecimal valor) {
        this.codigo = UUID.randomUUID().toString();
        this.dueno = dueno;
        this.titulo = titulo;
        this.valor = valor;
        this.estado = EstadoPublicacion.ACTIVA;
    }

    public static Publicacion publicar(UniCambista dueno, String titulo, BigDecimal valor) {
        if (!dueno.puedeCrearPublicacion()) {
            throw new ReglaDominioException("El UniCambista debe verificar su correo antes de publicar");
        }
        return new Publicacion(dueno, titulo, valor);
    }

    public void marcarEnProceso() {
        if (this.estado != EstadoPublicacion.ACTIVA) {
            throw new ReglaDominioException("Solo una publicación activa puede pasar a proceso de compra");
        }
        this.estado = EstadoPublicacion.EN_PROCESO;
    }

    public void eliminarLogicamente() {
        this.estado = EstadoPublicacion.ELIMINADA;
    }

    // ---- Único punto de entrada al Agregado para gestionar Ofertas ----

    public Oferta recibirOferta(UniCambista oferente, BigDecimal monto) {
        if (this.estado != EstadoPublicacion.ACTIVA) {
            throw new ReglaDominioException("Solo se puede ofertar sobre una publicación activa");
        }
        Oferta nueva = Oferta.crear(this, oferente, monto);
        this.ofertas.add(nueva);
        return nueva;
    }

    public void aceptarOferta(String idOferta, UniCambista dueno) {
        Oferta oferta = buscarOferta(idOferta);
        oferta.aceptar(dueno);
    }

    public void rechazarOferta(String idOferta, UniCambista dueno) {
        Oferta oferta = buscarOferta(idOferta);
        oferta.rechazar(dueno);
    }

    public void contraofertar(String idOferta, BigDecimal nuevoMonto) {
        Oferta oferta = buscarOferta(idOferta);
        oferta.contraofertar(nuevoMonto);
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