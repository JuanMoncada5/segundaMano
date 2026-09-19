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

        // El resto del flujo (Oferta -> Transaccion) se completa en el siguiente commit
    }
}