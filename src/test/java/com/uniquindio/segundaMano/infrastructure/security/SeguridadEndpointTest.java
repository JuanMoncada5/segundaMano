package com.uniquindio.segundaMano.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Punto 1: se prueba la SecurityFilterChain REAL (SecurityConfig), por eso @SpringBootTest
 * y no @WebMvcTest. La ruta elegida, POST /api/admin/publicaciones/expirar, es solo para ADMIN.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SeguridadEndpointTest {

    private static final String RUTA_ADMIN = "/api/admin/publicaciones/expirar";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sinTokenDeberiaResponder401() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA_ADMIN))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void conRolUniCambistaEnRutaDeAdminDeberiaResponder403() throws Exception {
        // Arrange: un usuario autenticado, pero con un rol que no alcanza
        var uniCambista = jwt().authorities(new SimpleGrantedAuthority("UNICAMBISTA"));

        // Act & Assert
        mockMvc.perform(post(RUTA_ADMIN).with(uniCambista))
                .andExpect(status().isForbidden());
    }

    @Test
    void conRolAdminDeberiaResponder200() throws Exception {
        // Arrange
        var admin = jwt().authorities(new SimpleGrantedAuthority("ADMIN"));

        // Act & Assert: en data.sql todas tienen actividad hoy, así que ninguna expira
        mockMvc.perform(post(RUTA_ADMIN).with(admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expiradas").value(0));
    }
}