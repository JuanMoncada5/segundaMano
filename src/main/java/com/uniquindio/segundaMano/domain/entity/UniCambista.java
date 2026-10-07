package com.uniquindio.segundaMano.domain.entity;

import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import com.uniquindio.segundaMano.domain.valueObject.RachaConfianza;
import com.uniquindio.segundaMano.domain.valueObject.EstadoTransaccion;

import java.util.Objects;

public class UniCambista {

    private final String correoInstitucional;
    private String nombre;
    private boolean verificado;
    private RachaConfianza rachaConfianza;

    private UniCambista(String correoInstitucional, String nombre) {
        this.correoInstitucional = correoInstitucional;
        this.nombre = nombre;
        this.verificado = false;
        this.rachaConfianza = RachaConfianza.inicial();
    }

    public static UniCambista registrar(String correoInstitucional, String nombre) {
        if (correoInstitucional == null || !correoInstitucional.endsWith("@uniquindio.edu.co")) {
            throw new ReglaDominioException("El correo debe ser institucional");
        }
        return new UniCambista(correoInstitucional, nombre);
    }

    public void verificarCorreo() {
        this.verificado = true;
    }

    public boolean puedeCrearPublicacion() {
        return this.verificado;
    }

    public void calificar(UniCambista otro, Transaccion transaccion, int estrellas) {
        if (transaccion.getEstado() != EstadoTransaccion.COMPLETADA) {
            throw new ReglaDominioException("Solo se pueden calificar Transacciones completadas");
        }
        boolean participaronAmbos =
                (this.equals(transaccion.getComprador()) && otro.equals(transaccion.getVendedor())) ||
                        (this.equals(transaccion.getVendedor()) && otro.equals(transaccion.getComprador()));
        if (!participaronAmbos) {
            throw new ReglaDominioException("Solo se califican Transacciones en las que se participó");
        }
        if (estrellas < 1 || estrellas > 5) {
            throw new ReglaDominioException("La calificación debe estar entre 1 y 5 estrellas");
        }
        otro.recibirCalificacion(estrellas);
    }

    private void recibirCalificacion(int estrellas) {
        int total = rachaConfianza.totalTransacciones();
        double nuevoPromedio = (rachaConfianza.promedioEstrellas() * total + estrellas) / (total + 1);
        this.rachaConfianza = new RachaConfianza(nuevoPromedio, total + 1);
    }

    public String getCorreoInstitucional() {
        return correoInstitucional;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isVerificado() {
        return verificado;
    }

    public RachaConfianza getRachaConfianza() {
        return rachaConfianza;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UniCambista otro)) return false;
        return Objects.equals(correoInstitucional, otro.correoInstitucional);
    }

    @Override
    public int hashCode() {
        return Objects.hash(correoInstitucional);
    }
}