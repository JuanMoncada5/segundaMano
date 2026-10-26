package com.uniquindio.segundaMano.infrastructure.rest.mapper;

import com.uniquindio.segundaMano.application.dto.AceptarOfertaRequest;
import com.uniquindio.segundaMano.application.dto.OfertarArticuloRequest;
import com.uniquindio.segundaMano.application.dto.PublicarArticuloRequest;
import com.uniquindio.segundaMano.infrastructure.rest.dto.AceptarOfertaBody;
import com.uniquindio.segundaMano.infrastructure.rest.dto.OfertarArticuloBody;
import com.uniquindio.segundaMano.infrastructure.rest.dto.PublicarArticuloBody;
import org.springframework.stereotype.Component;

/** Une lo que llega en la URL con lo que llega en el cuerpo para armar el Request del caso de uso. */
@Component
public class PublicacionRestMapper {

    public PublicarArticuloRequest toRequest(PublicarArticuloBody body) {
        return new PublicarArticuloRequest(
                body.correoDueno(), body.titulo(), body.valor(), body.curso(), body.semestre());
    }

    public OfertarArticuloRequest toRequest(String codigoPublicacion, OfertarArticuloBody body) {
        return new OfertarArticuloRequest(codigoPublicacion, body.correoOferente(), body.montoPropuesto());
    }

    public AceptarOfertaRequest toRequest(String codigoPublicacion, String idOferta, AceptarOfertaBody body) {
        return new AceptarOfertaRequest(codigoPublicacion, idOferta, body.correoDueno());
    }
}
