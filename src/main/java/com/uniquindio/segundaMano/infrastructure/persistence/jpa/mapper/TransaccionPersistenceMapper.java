package com.uniquindio.segundaMano.infrastructure.persistence.jpa.mapper;

import com.uniquindio.segundaMano.domain.entity.Publicacion;
import com.uniquindio.segundaMano.domain.entity.Transaccion;
import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.valueObject.EncuentroSeguro;
import com.uniquindio.segundaMano.infrastructure.persistence.jpa.entity.EncuentroSeguroEmbeddable;
import com.uniquindio.segundaMano.infrastructure.persistence.jpa.entity.TransaccionEntity;
import org.springframework.stereotype.Component;

/**
 * Traduce entre la Transaccion del dominio y su modelo JPA.
 * Quien llama debe entregarle ya resueltos la Publicacion y los UniCambistas.
 */
@Component
public class TransaccionPersistenceMapper {

    public TransaccionEntity toEntity(Transaccion transaccion) {
        EncuentroSeguro encuentro = transaccion.getEncuentroSeguro();
        EncuentroSeguroEmbeddable encuentroEmbeddable = (encuentro == null)
                ? null
                : new EncuentroSeguroEmbeddable(encuentro.zona(), encuentro.referencia());
        return new TransaccionEntity(
                transaccion.getId(),
                transaccion.getPublicacion().getCodigo(),
                transaccion.getComprador().getCorreoInstitucional(),
                transaccion.getVendedor().getCorreoInstitucional(),
                transaccion.getMontoAcordado(),
                encuentroEmbeddable,
                transaccion.isConfirmadaPorComprador(),
                transaccion.isConfirmadaPorVendedor(),
                transaccion.getEstado(),
                transaccion.isCalificadaPorComprador(),
                transaccion.isCalificadaPorVendedor()
        );
    }

    public Transaccion toDomain(TransaccionEntity entity, Publicacion publicacion,
                                UniCambista comprador, UniCambista vendedor) {
        EncuentroSeguroEmbeddable encuentro = entity.getEncuentroSeguro();
        EncuentroSeguro encuentroSeguro = (encuentro == null || encuentro.getZona() == null)
                ? null
                : new EncuentroSeguro(encuentro.getZona(), encuentro.getReferencia());
        return Transaccion.reconstruir(
                entity.getId(),
                publicacion,
                comprador,
                vendedor,
                entity.getMontoAcordado(),
                encuentroSeguro,
                entity.isConfirmadaPorComprador(),
                entity.isConfirmadaPorVendedor(),
                entity.getEstado(),
                entity.isCalificadaPorComprador(),
                entity.isCalificadaPorVendedor()
        );
    }
}
