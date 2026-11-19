package com.uniquindio.segundaMano.application.usecase;

import com.uniquindio.segundaMano.application.dto.CalificarUniCambistaRequest;
import com.uniquindio.segundaMano.application.dto.UniCambistaResponse;
import com.uniquindio.segundaMano.domain.entity.Transaccion;
import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.segundaMano.domain.repository.TransaccionRepository;
import com.uniquindio.segundaMano.domain.repository.UniCambistaRepository;

/**
 * Funcionalidad propia: después de una Transacción COMPLETADA, cada participante califica al otro
 * (de 1 a 5 estrellas) y eso actualiza su RachaConfianza.
 * Todas las reglas ya viven en el dominio (UniCambista.calificar): aquí solo se busca, se pide y se guarda.
 */
public class CalificarUniCambistaUseCase {

    private final TransaccionRepository transaccionRepository;
    private final UniCambistaRepository uniCambistaRepository;

    public CalificarUniCambistaUseCase(TransaccionRepository transaccionRepository,
                                       UniCambistaRepository uniCambistaRepository) {
        this.transaccionRepository = transaccionRepository;
        this.uniCambistaRepository = uniCambistaRepository;
    }

    /** @return el UniCambista calificado, con su RachaConfianza ya actualizada */
    public UniCambistaResponse ejecutar(CalificarUniCambistaRequest request) {
        Transaccion transaccion = transaccionRepository.buscarPorId(request.idTransaccion())
                .orElseThrow(() -> new RecursoNoEncontradoException("La Transacción no existe"));
        UniCambista calificador = uniCambistaRepository.buscarPorCorreo(request.correoCalificador())
                .orElseThrow(() -> new RecursoNoEncontradoException("El UniCambista no está registrado"));

        // Se califica "al otro": si quien califica es el comprador, al vendedor, y al revés.
        // Si no participó, el dominio lo rechaza en calificar().
        UniCambista calificado = calificador.equals(transaccion.getComprador())
                ? transaccion.getVendedor()
                : transaccion.getComprador();

        calificador.calificar(calificado, transaccion, request.estrellas());

        uniCambistaRepository.guardar(calificado);   // su RachaConfianza cambió
        transaccionRepository.guardar(transaccion);  // quedó marcado quién ya calificó
        return UniCambistaResponse.desde(calificado);
    }
}