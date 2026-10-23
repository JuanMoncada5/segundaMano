package com.uniquindio.segundaMano.infrastructure.persistence.jpa.entity;

import com.uniquindio.segundaMano.domain.valueObject.EstadoOferta;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/**
 * Modelo JPA de la Oferta (entidad interna del Agregado Publicacion).
 * Guarda el código de su Publicación y el correo del oferente en vez de objetos completos.
 */
@Entity
@Table(name = "ofertas")
public class OfertaEntity {

    @Id
    private String id;

    private String publicacionCodigo;

    private String oferenteCorreo;

    @Column(precision = 15, scale = 2)
    private BigDecimal montoPropuesto;

    @Enumerated(EnumType.STRING)
    private EstadoOferta estado;

    private boolean transaccionIniciada;

    /** JPA exige un constructor vacío. */
    protected OfertaEntity() {
    }

    public OfertaEntity(String id, String publicacionCodigo, String oferenteCorreo, BigDecimal montoPropuesto, EstadoOferta estado, boolean transaccionIniciada) {
        this.id = id;
        this.publicacionCodigo = publicacionCodigo;
        this.oferenteCorreo = oferenteCorreo;
        this.montoPropuesto = montoPropuesto;
        this.estado = estado;
        this.transaccionIniciada = transaccionIniciada;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublicacionCodigo() {
        return publicacionCodigo;
    }

    public void setPublicacionCodigo(String publicacionCodigo) {
        this.publicacionCodigo = publicacionCodigo;
    }

    public String getOferenteCorreo() {
        return oferenteCorreo;
    }

    public void setOferenteCorreo(String oferenteCorreo) {
        this.oferenteCorreo = oferenteCorreo;
    }

    public BigDecimal getMontoPropuesto() {
        return montoPropuesto;
    }

    public void setMontoPropuesto(BigDecimal montoPropuesto) {
        this.montoPropuesto = montoPropuesto;
    }

    public EstadoOferta getEstado() {
        return estado;
    }

    public void setEstado(EstadoOferta estado) {
        this.estado = estado;
    }

    public boolean isTransaccionIniciada() {
        return transaccionIniciada;
    }

    public void setTransaccionIniciada(boolean transaccionIniciada) {
        this.transaccionIniciada = transaccionIniciada;
    }
}
