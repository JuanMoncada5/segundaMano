package com.uniquindio.segundaMano.infrastructure.persistence.jpa.entity;

import com.uniquindio.segundaMano.domain.valueObject.EstadoTransaccion;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/**
 * Modelo JPA de la Transacción. Referencia a la Publicación y a los UniCambistas por su id.
 */
@Entity
@Table(name = "transacciones")
public class TransaccionEntity {

    @Id
    private String id;

    private String publicacionCodigo;

    private String compradorCorreo;

    private String vendedorCorreo;

    @Column(precision = 15, scale = 2)
    private BigDecimal montoAcordado;

    @Embedded
    private EncuentroSeguroEmbeddable encuentroSeguro;

    private boolean confirmadaPorComprador;

    private boolean confirmadaPorVendedor;

    @Enumerated(EnumType.STRING)
    private EstadoTransaccion estado;

    private boolean calificadaPorComprador;

    private boolean calificadaPorVendedor;

    /** JPA exige un constructor vacío. */
    protected TransaccionEntity() {
    }

    public TransaccionEntity(String id, String publicacionCodigo, String compradorCorreo, String vendedorCorreo, BigDecimal montoAcordado, EncuentroSeguroEmbeddable encuentroSeguro, boolean confirmadaPorComprador, boolean confirmadaPorVendedor, EstadoTransaccion estado, boolean calificadaPorComprador, boolean calificadaPorVendedor) {
        this.id = id;
        this.publicacionCodigo = publicacionCodigo;
        this.compradorCorreo = compradorCorreo;
        this.vendedorCorreo = vendedorCorreo;
        this.montoAcordado = montoAcordado;
        this.encuentroSeguro = encuentroSeguro;
        this.confirmadaPorComprador = confirmadaPorComprador;
        this.confirmadaPorVendedor = confirmadaPorVendedor;
        this.estado = estado;
        this.calificadaPorComprador = calificadaPorComprador;
        this.calificadaPorVendedor = calificadaPorVendedor;
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

    public String getCompradorCorreo() {
        return compradorCorreo;
    }

    public void setCompradorCorreo(String compradorCorreo) {
        this.compradorCorreo = compradorCorreo;
    }

    public String getVendedorCorreo() {
        return vendedorCorreo;
    }

    public void setVendedorCorreo(String vendedorCorreo) {
        this.vendedorCorreo = vendedorCorreo;
    }

    public BigDecimal getMontoAcordado() {
        return montoAcordado;
    }

    public void setMontoAcordado(BigDecimal montoAcordado) {
        this.montoAcordado = montoAcordado;
    }

    public EncuentroSeguroEmbeddable getEncuentroSeguro() {
        return encuentroSeguro;
    }

    public void setEncuentroSeguro(EncuentroSeguroEmbeddable encuentroSeguro) {
        this.encuentroSeguro = encuentroSeguro;
    }

    public boolean isConfirmadaPorComprador() {
        return confirmadaPorComprador;
    }

    public void setConfirmadaPorComprador(boolean confirmadaPorComprador) {
        this.confirmadaPorComprador = confirmadaPorComprador;
    }

    public boolean isConfirmadaPorVendedor() {
        return confirmadaPorVendedor;
    }

    public void setConfirmadaPorVendedor(boolean confirmadaPorVendedor) {
        this.confirmadaPorVendedor = confirmadaPorVendedor;
    }

    public EstadoTransaccion getEstado() {
        return estado;
    }

    public void setEstado(EstadoTransaccion estado) {
        this.estado = estado;
    }

    public boolean isCalificadaPorComprador() {
        return calificadaPorComprador;
    }

    public void setCalificadaPorComprador(boolean calificadaPorComprador) {
        this.calificadaPorComprador = calificadaPorComprador;
    }

    public boolean isCalificadaPorVendedor() {
        return calificadaPorVendedor;
    }

    public void setCalificadaPorVendedor(boolean calificadaPorVendedor) {
        this.calificadaPorVendedor = calificadaPorVendedor;
    }
}
