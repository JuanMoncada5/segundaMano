package com.uniquindio.segundaMano.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Embeddable;

/**
 * Value Object VigenciaAcademica guardado como dos columnas dentro de la tabla de la Publicación.
 * Si la Publicación no tiene vigencia, las dos columnas quedan en null.
 */
@Embeddable
public class VigenciaAcademicaEmbeddable {

    private String curso;

    private String semestre;

    /** JPA exige un constructor vacío. */
    protected VigenciaAcademicaEmbeddable() {
    }

    public VigenciaAcademicaEmbeddable(String curso, String semestre) {
        this.curso = curso;
        this.semestre = semestre;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }
}
