package com.uniquindio.segundaMano.domain.exception;

/**
 * Se lanza cuando se busca algo que no existe (una Publicación, una Transacción o un UniCambista).
 * Hereda de ReglaDominioException para que el código que ya la capturaba siga funcionando,
 * pero el @RestControllerAdvice la reconoce aparte y responde 404 en vez de 400.
 */
public class RecursoNoEncontradoException extends ReglaDominioException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}