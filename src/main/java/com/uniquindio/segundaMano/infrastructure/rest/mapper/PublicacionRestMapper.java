package com.uniquindio.segundaMano.infrastructure.rest.mapper;

import com.uniquindio.segundaMano.application.dto.AceptarOfertaRequest;
import com.uniquindio.segundaMano.application.dto.OfertarArticuloRequest;
import com.uniquindio.segundaMano.application.dto.PublicarArticuloRequest;
import com.uniquindio.segundaMano.infrastructure.rest.dto.OfertarArticuloBody;
import com.uniquindio.segundaMano.infrastructure.rest.dto.PublicarArticuloBody;
import org.springframework.stereotype.Component;

/** Une lo que llega en la URL, en el cuerpo y en el token para armar el Request del caso de uso. */
@Component
public class PublicacionRestMapper {

    /** El correo del dueño viene del token (jwt.sub), nunca del cuerpo. */
    public PublicarArticuloRequest toRequest(String correoDueno, PublicarArticuloBody body) {
        return new PublicarArticuloRequest(
                correoDueno, body.titulo(), body.valor(), body.curso(), body.semestre());
    }

    /** El correo del oferente viene del token (jwt.sub), nunca del cuerpo. */
    public OfertarArticuloRequest toRequest(String codigoPublicacion, String correoOferente, OfertarArticuloBody body) {
        return new OfertarArticuloRequest(codigoPublicacion, correoOferente, body.montoPropuesto());
    }

    /** Aceptar no necesita cuerpo: el dueño que acepta viene del token (jwt.sub). */
    public AceptarOfertaRequest toRequest(String codigoPublicacion, String idOferta, String correoDueno) {
        return new AceptarOfertaRequest(codigoPublicacion, idOferta, correoDueno);
    }
}
