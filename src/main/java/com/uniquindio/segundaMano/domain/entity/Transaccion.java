package com.uniquindio.segundaMano.domain.entity;

import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import com.uniquindio.segundaMano.domain.valueObject.EstadoOferta;
import com.uniquindio.segundaMano.domain.valueObject.EstadoTransaccion;
import com.uniquindio.segundaMano.domain.valueObject.EncuentroSeguro;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Transaccion {

    private final String id;
    private final Publicacion publicacion;
    private final UniCambista comprador;
    private final UniCambista vendedor;
    private final BigDecimal montoAcordado;
    private EncuentroSeguro encuentroSeguro;
    private boolean confirmadaPorComprador;
    private boolean confirmadaPorVendedor;
    private EstadoTransaccion estado;
    private boolean calificadaPorComprador;
    private boolean calificadaPorVendedor;

    private Transaccion(Publicacion publicacion, UniCambista comprador, UniCambista vendedor, BigDecimal montoAcordado) {
        this.id = UUID.randomUUID().toString();
        this.publicacion = publicacion;
        this.comprador = comprador;
        this.vendedor = vendedor;
        this.montoAcordado = montoAcordado; // congelado: no cambia aunque la Oferta o Publicacion cambien luego
        this.confirmadaPorComprador = false;
        this.confirmadaPorVendedor = false;
        this.estado = EstadoTransaccion.PENDIENTE;
    }

    public static Transaccion iniciar(Oferta ofertaAceptada) {
        if (ofertaAceptada == null || ofertaAceptada.getEstado() != EstadoOferta.ACEPTADA) {
            throw new ReglaDominioException("Solo se puede iniciar una Transacción a partir de una Oferta aceptada");
        }
        ofertaAceptada.vincularTransaccion(); // una Oferta solo puede originar una Transacción
        return new Transaccion(
                ofertaAceptada.getPublicacion(),
                ofertaAceptada.getOferente(),
                ofertaAceptada.getPublicacion().getDueno(),
                ofertaAceptada.getMontoPropuesto()
        );
    }

    public void asignarEncuentroSeguro(EncuentroSeguro encuentroSeguro) {
        if (encuentroSeguro == null) {
            throw new ReglaDominioException("El EncuentroSeguro es obligatorio");
        }
        if (estado == EstadoTransaccion.COMPLETADA) {
            throw new ReglaDominioException("No se puede cambiar el EncuentroSeguro de una Transacción Completada");
        }
        this.encuentroSeguro = encuentroSeguro;
    }

    public void confirmar(UniCambista quienConfirma) {
        if (quienConfirma == null) {
            throw new ReglaDominioException("Debe indicarse quién confirma la Transacción");
        }
        if (estado == EstadoTransaccion.COMPLETADA) {
            throw new ReglaDominioException("La Transacción ya está Completada");
        }
        if (quienConfirma.equals(comprador)) {
            this.confirmadaPorComprador = true;
        } else if (quienConfirma.equals(vendedor)) {
            this.confirmadaPorVendedor = true;
        } else {
            throw new ReglaDominioException("Solo el comprador o el vendedor de esta Transacción pueden confirmarla");
        }
        if (confirmadaPorComprador && confirmadaPorVendedor) {
            this.estado = EstadoTransaccion.COMPLETADA;
        }
    }

    /**
     * Lo invoca UniCambista.calificar: cada participante califica una sola vez por Transacción.
     */
    void registrarCalificacionDe(UniCambista quienCalifica) {
        if (quienCalifica.equals(comprador)) {
            if (calificadaPorComprador) {
                throw new ReglaDominioException("El comprador ya calificó esta Transacción");
            }
            calificadaPorComprador = true;
        } else if (quienCalifica.equals(vendedor)) {
            if (calificadaPorVendedor) {
                throw new ReglaDominioException("El vendedor ya calificó esta Transacción");
            }
            calificadaPorVendedor = true;
        } else {
            throw new ReglaDominioException("Solo se califican Transacciones en las que se participó");
        }
    }

    public String getId() {
        return id;
    }

    public Publicacion getPublicacion() {
        return publicacion;
    }

    public UniCambista getComprador() {
        return comprador;
    }

    public UniCambista getVendedor() {
        return vendedor;
    }

    public BigDecimal getMontoAcordado() {
        return montoAcordado;
    }

    public EncuentroSeguro getEncuentroSeguro() {
        return encuentroSeguro;
    }

    public EstadoTransaccion getEstado() {
        return estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaccion otra)) return false;
        return Objects.equals(id, otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}