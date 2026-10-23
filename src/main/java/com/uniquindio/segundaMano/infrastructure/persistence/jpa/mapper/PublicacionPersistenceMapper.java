package com.uniquindio.segundaMano.infrastructure.persistence.jpa.mapper;

import com.uniquindio.segundaMano.domain.entity.Oferta;
import com.uniquindio.segundaMano.domain.entity.Publicacion;
import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.valueObject.VigenciaAcademica;
import com.uniquindio.segundaMano.infrastructure.persistence.jpa.entity.OfertaEntity;
import com.uniquindio.segundaMano.infrastructure.persistence.jpa.entity.PublicacionEntity;
import com.uniquindio.segundaMano.infrastructure.persistence.jpa.entity.VigenciaAcademicaEmbeddable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Traduce entre el Agregado Publicacion (con sus Ofertas) y su modelo JPA.
 * Quien llama debe entregarle ya resueltos los UniCambistas (dueño y oferentes).
 */
@Component
public class PublicacionPersistenceMapper {

    public PublicacionEntity toEntity(Publicacion publicacion) {
        VigenciaAcademica vigencia = publicacion.getVigenciaAcademica();
        VigenciaAcademicaEmbeddable vigenciaEmbeddable = (vigencia == null)
                ? null
                : new VigenciaAcademicaEmbeddable(vigencia.curso(), vigencia.semestre());
        return new PublicacionEntity(
                publicacion.getCodigo(),
                publicacion.getDueno().getCorreoInstitucional(),
                publicacion.getTitulo(),
                publicacion.getValor(),
                publicacion.getEstado(),
                vigenciaEmbeddable,
                publicacion.getFechaUltimaActividad()
        );
    }

    public List<OfertaEntity> toOfertaEntities(Publicacion publicacion) {
        return publicacion.getOfertas().stream()
                .map(oferta -> toOfertaEntity(publicacion, oferta))
                .toList();
    }

    private OfertaEntity toOfertaEntity(Publicacion publicacion, Oferta oferta) {
        return new OfertaEntity(
                oferta.getId(),
                publicacion.getCodigo(),
                oferta.getOferente().getCorreoInstitucional(),
                oferta.getMontoPropuesto(),
                oferta.getEstado(),
                oferta.tieneTransaccionIniciada()
        );
    }

    public Publicacion.DatosOferta toDatosOferta(OfertaEntity entity, UniCambista oferente) {
        return new Publicacion.DatosOferta(
                entity.getId(),
                oferente,
                entity.getMontoPropuesto(),
                entity.getEstado(),
                entity.isTransaccionIniciada()
        );
    }

    public Publicacion toDomain(PublicacionEntity entity, UniCambista dueno, List<Publicacion.DatosOferta> ofertas) {
        VigenciaAcademicaEmbeddable vigencia = entity.getVigenciaAcademica();
        VigenciaAcademica vigenciaAcademica = (vigencia == null || vigencia.getCurso() == null)
                ? null
                : new VigenciaAcademica(vigencia.getCurso(), vigencia.getSemestre());
        return Publicacion.reconstruir(
                entity.getCodigo(),
                dueno,
                entity.getTitulo(),
                entity.getValor(),
                entity.getEstado(),
                vigenciaAcademica,
                entity.getFechaUltimaActividad(),
                ofertas
        );
    }
}
