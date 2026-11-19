package com.uniquindio.segundaMano.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Antes de una petición con token, el navegador pregunta con un OPTIONS ("preflight")
 * si el origen de Angular tiene permiso. El backend debe responder que sí.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CorsTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void elOrigenDeAngularTienePermiso() throws Exception {
        mockMvc.perform(options("/api/publicaciones/buscar")
                        .header("Origin", "http://localhost:4200")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "Authorization"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }

    @Test
    void unOrigenDesconocidoNoTienePermiso() throws Exception {
        mockMvc.perform(options("/api/publicaciones/buscar")
                        .header("Origin", "http://sitio-malicioso.com")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden());
    }
}