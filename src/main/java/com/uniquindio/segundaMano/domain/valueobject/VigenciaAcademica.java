package com.uniquindio.segundaMano.domain.valueObject;

import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;

public record VigenciaAcademica(String curso, String semestre) {

    public VigenciaAcademica {
        if (curso == null || curso.isBlank()) {
            throw new ReglaDominioException("La VigenciaAcademica debe indicar el curso");
        }
        if (semestre == null || semestre.isBlank()) {
            throw new ReglaDominioException("La VigenciaAcademica debe indicar el semestre");
        }
    }

    @Override
    public String toString() {
        return curso + " - " + semestre;
    }
}