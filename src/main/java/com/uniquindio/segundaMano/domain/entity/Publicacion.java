package com.uniquindio.segundaMano.domain.entity;

import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import com.uniquindio.segundaMano.domain.valueobject.EstadoPublicacion;
import com.uniquindio.segundaMano.domain.valueobject.VigenciaAcademica;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Publicacion {

    private final String codigo;
    private final UniCambista dueno;
    private String titulo;
    private BigDecimal valor;
    private EstadoPublicacion estado;

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

    // nuevo campo
    private VigenciaAcademica vigenciaAcademica; // puede quedar en null

    // nuevo método
    public void asociarVigenciaAcademica(VigenciaAcademica vigenciaAcademica) {
        this.vigenciaAcademica = vigenciaAcademica;
    }

    public String obtenerEtiquetaVisibilidad() {
        return (vigenciaAcademica != null) ? vigenciaAcademica.toString() : null;
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
