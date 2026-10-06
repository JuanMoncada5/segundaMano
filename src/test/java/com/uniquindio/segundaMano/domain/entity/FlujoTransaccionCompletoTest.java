package com.uniquindio.segundaMano.domain.entity;

import com.uniquindio.segundaMano.domain.valueObject.EncuentroSeguro;
import com.uniquindio.segundaMano.domain.valueObject.EstadoOferta;
import com.uniquindio.segundaMano.domain.valueObject.EstadoPublicacion;
import com.uniquindio.segundaMano.domain.valueObject.EstadoTransaccion;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FlujoTransaccionCompletoTest {

    @Test
    void flujoCompleto_publicacionOfertaYTransaccion() {
        // 1. Se registran y verifican los dos UniCambistas
        UniCambista vendedor = UniCambista.registrar("vendedor@uniquindio.edu.co", "Laura");
        vendedor.verificarCorreo();

        UniCambista comprador = UniCambista.registrar("comprador@uniquindio.edu.co", "Juan");
        comprador.verificarCorreo();

        // 2. El vendedor publica un artículo
        Publicacion publicacion = Publicacion.publicar(vendedor, "Calculadora Casio", new BigDecimal("150000"));
        assertEquals(EstadoPublicacion.ACTIVA, publicacion.getEstado());

        // 3. El comprador hace una oferta válida (>= 50% del precio) a través de la Publicacion (raíz del agregado)
        Oferta oferta = publicacion.recibirOferta(comprador, new BigDecimal("100000"));
        assertEquals(EstadoOferta.PENDIENTE, oferta.getEstado());

        // 4. El vendedor acepta la oferta, también a través de la Publicacion
        publicacion.aceptarOferta(oferta.getId(), vendedor);
        assertEquals(EstadoOferta.ACEPTADA, oferta.getEstado());
        assertEquals(EstadoPublicacion.EN_PROCESO, publicacion.getEstado());

        // 5. Se inicia la Transaccion a partir de la Oferta aceptada (Agregado independiente)
        Transaccion transaccion = Transaccion.iniciar(oferta);
        assertEquals(EstadoTransaccion.PENDIENTE, transaccion.getEstado());

        // 6. Se asigna un EncuentroSeguro
        transaccion.asignarEncuentroSeguro(new EncuentroSeguro("Biblioteca Central"));

        // 7. Ambos confirman -> la Transaccion queda Completada
        transaccion.confirmar(comprador);
        assertEquals(EstadoTransaccion.PENDIENTE, transaccion.getEstado()); // aún falta el vendedor

        transaccion.confirmar(vendedor);
        assertEquals(EstadoTransaccion.COMPLETADA, transaccion.getEstado());
    }
}