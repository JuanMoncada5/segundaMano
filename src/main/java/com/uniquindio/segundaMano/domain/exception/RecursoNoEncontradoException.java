package com.uniquindio.segundaMano.domain.exception;

/** Se lanza cuando algo que se busca por su identificador no existe. La capa REST la traduce a HTTP 404. */
public class RecursoNoEncontradoException extends ReglaDominioException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
