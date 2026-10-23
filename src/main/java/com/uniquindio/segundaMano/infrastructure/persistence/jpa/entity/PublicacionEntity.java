package com.uniquindio.segundaMano.infrastructure.persistence.jpa.entity;

import com.uniquindio.segundaMano.domain.valueObject.EstadoPublicacion;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Modelo JPA de la Publicación. El dueño se guarda por su correo (los Agregados se referencian por id).
 */
@Entity
@Table(name = "publicaciones")
public class PublicacionEntity {

    @Id
    private String codigo;

    private String duenoCorreo;

    private String titulo;

    @Column(precision = 15, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    private EstadoPublicacion estado;

    @Embedded
    private VigenciaAcademicaEmbeddable vigenciaAcademica;

    private LocalDate fechaUltimaActividad;

    /** JPA exige un constructor vacío. */
    protected PublicacionEntity() {
    }

    public PublicacionEntity(String codigo, String duenoCorreo, String titulo, BigDecimal valor, EstadoPublicacion estado, VigenciaAcademicaEmbeddable vigenciaAcademica, LocalDate fechaUltimaActividad) {
        this.codigo = codigo;
        this.duenoCorreo = duenoCorreo;
        this.titulo = titulo;
        this.valor = valor;
        this.estado = estado;
        this.vigenciaAcademica = vigenciaAcademica;
        this.fechaUltimaActividad = fechaUltimaActividad;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDuenoCorreo() {
        return duenoCorreo;
    }

    public void setDuenoCorreo(String duenoCorreo) {
        this.duenoCorreo = duenoCorreo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public EstadoPublicacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoPublicacion estado) {
        this.estado = estado;
    }

    public VigenciaAcademicaEmbeddable getVigenciaAcademica() {
        return vigenciaAcademica;
    }

    public void setVigenciaAcademica(VigenciaAcademicaEmbeddable vigenciaAcademica) {
        this.vigenciaAcademica = vigenciaAcademica;
    }

    public LocalDate getFechaUltimaActividad() {
        return fechaUltimaActividad;
    }

    public void setFechaUltimaActividad(LocalDate fechaUltimaActividad) {
        this.fechaUltimaActividad = fechaUltimaActividad;
    }
}
