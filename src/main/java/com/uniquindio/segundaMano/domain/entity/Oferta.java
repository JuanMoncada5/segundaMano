package com.uniquindio.segundaMano.domain.entity;

import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import com.uniquindio.segundaMano.domain.valueObject.EstadoOferta;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad interna del Agregado Publicacion.
 * Todos sus métodos de cambio son package-private: solo Publicacion (la raíz)
 * puede cambiar su estado. La excepción es vincularTransaccion(), que la usa
 * Transaccion.iniciar para garantizar una sola Transacción por Oferta.
 */
public class Oferta {

    private static final BigDecimal PORCENTAJE_MINIMO = new BigDecimal("0.5");

    private final String id;
    private final Publicacion publicacion;
    private final UniCambista oferente;
    private BigDecimal montoPropuesto;
    private EstadoOferta estado;
    private boolean transaccionIniciada;

    private Oferta(Publicacion publicacion, UniCambista oferente, BigDecimal montoPropuesto) {
        this.id = UUID.randomUUID().toString();
        this.publicacion = publicacion;
        this.oferente = oferente;
        this.montoPropuesto = montoPropuesto;
        this.estado = EstadoOferta.PENDIENTE;
        this.transaccionIniciada = false;
    }

    static Oferta crear(Publicacion publicacion, UniCambista oferente, BigDecimal montoPropuesto) {
        if (oferente == null) {
            throw new ReglaDominioException("La Oferta debe tener un oferente");
        }
        if (oferente.equals(publicacion.getDueno())) {
            throw new ReglaDominioException("El dueño de la publicación no puede ofertar sobre su propia publicación");
        }
        Oferta oferta = new Oferta(publicacion, oferente, montoPropuesto);
        oferta.validarMontoMinimo(montoPropuesto);
        return oferta;
    }

    private void validarMontoMinimo(BigDecimal monto) {
        if (monto == null) {
            throw new ReglaDominioException("El monto de la Oferta es obligatorio");
        }
        BigDecimal minimo = publicacion.getValor().multiply(PORCENTAJE_MINIMO);
        if (monto.compareTo(minimo) < 0) {
            throw new ReglaDominioException("El monto no puede ser menor al 50% del precio original");
        }
    }

    /** El dueño acepta una Oferta que sigue PENDIENTE. */
    void aceptar(UniCambista dueno) {
        validarDueno(dueno);
        validarEstado(EstadoOferta.PENDIENTE, "Solo se puede aceptar una Oferta pendiente");
        this.estado = EstadoOferta.ACEPTADA;
    }

    /** El dueño rechaza una Oferta que sigue PENDIENTE. */
    void rechazar(UniCambista dueno) {
        validarDueno(dueno);
        validarEstado(EstadoOferta.PENDIENTE, "Solo se puede rechazar una Oferta pendiente");
        this.estado = EstadoOferta.RECHAZADA;
    }

    /** El dueño responde a una Oferta PENDIENTE con un nuevo monto (mínimo 50% del precio). */
    void contraofertar(UniCambista dueno, BigDecimal nuevoMonto) {
        validarDueno(dueno);
        validarEstado(EstadoOferta.PENDIENTE, "Solo se puede contraofertar una Oferta pendiente");
        validarMontoMinimo(nuevoMonto);
        this.montoPropuesto = nuevoMonto;
        this.estado = EstadoOferta.CONTRAOFERTADA;
    }

    /** El oferente (comprador) acepta la contraoferta que le hizo el dueño. */
    void aceptarContraoferta(UniCambista quienAcepta) {
        if (quienAcepta == null || !quienAcepta.equals(oferente)) {
            throw new ReglaDominioException("Solo quien hizo la Oferta puede aceptar la contraoferta");
        }
        validarEstado(EstadoOferta.CONTRAOFERTADA, "Solo se puede aceptar una Oferta contraofertada");
        this.estado = EstadoOferta.ACEPTADA;
    }

    /** Lo invoca Transaccion.iniciar: una Oferta aceptada solo puede originar una Transacción. */
    void vincularTransaccion() {
        if (this.transaccionIniciada) {
            throw new ReglaDominioException("Esta Oferta ya tiene una Transacción iniciada");
        }
        this.transaccionIniciada = true;
    }

    private void validarEstado(EstadoOferta esperado, String mensaje) {
        if (this.estado != esperado) {
            throw new ReglaDominioException(mensaje);
        }
    }

    private void validarDueno(UniCambista dueno) {
        if (dueno == null || !dueno.equals(publicacion.getDueno())) {
            throw new ReglaDominioException("Solo el dueño de la publicación puede aceptar, rechazar o contraofertar la oferta");
        }
    }

    public String getId() {
        return id;
    }

    public Publicacion getPublicacion() {
        return publicacion;
    }

    public UniCambista getOferente() {
        return oferente;
    }

    public BigDecimal getMontoPropuesto() {
        return montoPropuesto;
    }

    public EstadoOferta getEstado() {
        return estado;
    }

    public boolean tieneTransaccionIniciada() {
        return transaccionIniciada;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Oferta otra)) return false;
        return Objects.equals(id, otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
