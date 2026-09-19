package com.uniquindio.segundaMano.domain.entity;

import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import com.uniquindio.segundaMano.domain.valueObject.EstadoOferta;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Oferta {

    private static final BigDecimal PORCENTAJE_MINIMO = new BigDecimal("0.5");

    private final String id;
    private final Publicacion publicacion;
    private final UniCambista oferente;
    private BigDecimal montoPropuesto;
    private EstadoOferta estado;

    private Oferta(Publicacion publicacion, UniCambista oferente, BigDecimal montoPropuesto) {
        this.id = UUID.randomUUID().toString();
        this.publicacion = publicacion;
        this.oferente = oferente;
        this.montoPropuesto = montoPropuesto;
        this.estado = EstadoOferta.PENDIENTE;
    }

    static Oferta crear(Publicacion publicacion, UniCambista oferente, BigDecimal montoPropuesto) {
        if (oferente.equals(publicacion.getDueno())) {
            throw new ReglaDominioException("El dueño de la publicación no puede ofertar sobre su propia publicación");
        }
        Oferta oferta = new Oferta(publicacion, oferente, montoPropuesto);
        oferta.validarMontoMinimo(montoPropuesto);
        return oferta;
    }

    private void validarMontoMinimo(BigDecimal monto) {
        BigDecimal minimo = publicacion.getValor().multiply(PORCENTAJE_MINIMO);
        if (monto.compareTo(minimo) < 0) {
            throw new ReglaDominioException("El monto no puede ser menor al 50% del precio original");
        }
    }

    public void aceptar(UniCambista dueno) {
        validarDueno(dueno);
        this.estado = EstadoOferta.ACEPTADA;
        this.publicacion.marcarEnProceso();
    }

    public void rechazar(UniCambista dueno) {
        validarDueno(dueno);
        this.estado = EstadoOferta.RECHAZADA;
    }

    public void contraofertar(BigDecimal nuevoMonto) {
        if (this.estado != EstadoOferta.PENDIENTE) {
            throw new ReglaDominioException("Solo se puede contraofertar una Oferta pendiente");
        }
        validarMontoMinimo(nuevoMonto);
        this.montoPropuesto = nuevoMonto;
        this.estado = EstadoOferta.CONTRAOFERTADA;
    }

    private void validarDueno(UniCambista dueno) {
        if (!dueno.equals(publicacion.getDueno())) {
            throw new ReglaDominioException("Solo el dueño de la publicación puede aceptar o rechazar la oferta");
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
