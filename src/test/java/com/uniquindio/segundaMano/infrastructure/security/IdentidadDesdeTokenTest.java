package com.uniquindio.segundaMano.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Quién actúa sale SIEMPRE del token (claim "sub"), nunca del cuerpo de la petición.
 * Se usa la seguridad real (SecurityConfig) y los datos de data.sql.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class IdentidadDesdeTokenTest {

    @Autowired
    private MockMvc mockMvc;

    private static org.springframework.test.web.servlet.request.RequestPostProcessor comoUsuario(String correo) {
        return jwt().jwt(token -> token.subject(correo))
                .authorities(new SimpleGrantedAuthority("UNICAMBISTA"));
    }

    @Test
    void elDuenoDeLaPublicacionEsQuienTieneElTokenAunqueElCuerpoDigaOtraCosa() throws Exception {
        // Arrange: un cuerpo con un correoDueno falso (el servidor debe ignorarlo)
        String cuerpo = """
                { "correoDueno": "luis@uqvirtual.edu.co", "titulo": "Regla de dibujo", "valor": 12000 }
                """;

        // Act & Assert: la publicación queda a nombre de Ana, la del token
        mockMvc.perform(post("/api/publicaciones")
                        .with(comoUsuario("ana@uqvirtual.edu.co"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.correoDueno").value("ana@uqvirtual.edu.co"));
    }

    @Test
    void publicarSinTokenResponde401() throws Exception {
        mockMvc.perform(post("/api/publicaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "titulo": "Regla de dibujo", "valor": 12000 }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ofertarUsaElCorreoDelTokenComoOferente() throws Exception {
        // PUB-004 (60.000) es de Ana y no tiene ofertas; Sofía oferta 45.000
        mockMvc.perform(post("/api/publicaciones/PUB-004/ofertas")
                        .with(comoUsuario("sofia@uqvirtual.edu.co"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "montoPropuesto": 45000 }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.correoOferente").value("sofia@uqvirtual.edu.co"));
    }

    @Test
    void quienNoEsElDuenoNoPuedeAceptarUnaOfertaAunqueConozcaElCorreoDelDueno() throws Exception {
        // OF-001 es una oferta sobre PUB-001, que es de Ana. Luis intenta aceptarla: la regla de dominio lo rechaza.
        mockMvc.perform(put("/api/publicaciones/PUB-001/ofertas/OF-001/aceptar")
                        .with(comoUsuario("luis@uqvirtual.edu.co")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void elDuenoSiPuedeAceptarUnaOfertaSinEnviarCuerpo() throws Exception {
        mockMvc.perform(put("/api/publicaciones/PUB-001/ofertas/OF-001/aceptar")
                        .with(comoUsuario("ana@uqvirtual.edu.co")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }
}
