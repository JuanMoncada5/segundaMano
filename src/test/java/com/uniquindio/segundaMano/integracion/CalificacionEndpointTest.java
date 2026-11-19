package com.uniquindio.segundaMano.integracion;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Funcionalidad propia (calificar) con la seguridad REAL y los datos de data.sql:
 * TRX-001 está COMPLETADA (luis compró a ana) y TRX-002 está PENDIENTE (carlos y sofia).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CalificacionEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    private RequestPostProcessor usuario(String correo) {
        return jwt().jwt(token -> token.subject(correo)).authorities(new SimpleGrantedAuthority("UNICAMBISTA"));
    }

    @Test
    void elCompradorCalificaUnaVezYLaSegundaEsRechazada() throws Exception {
        // Act & Assert: luis califica a ana con 5 estrellas (ana tenía 5 transacciones en data.sql)
        mockMvc.perform(post("/api/transacciones/TRX-001/calificacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estrellas\": 5}")
                        .with(usuario("luis@uqvirtual.edu.co")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correoInstitucional").value("ana@uqvirtual.edu.co"))
                .andExpect(jsonPath("$.totalTransacciones").value(6));

        // La segunda vez la regla de dominio lo impide
        mockMvc.perform(post("/api/transacciones/TRX-001/calificacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estrellas\": 1}")
                        .with(usuario("luis@uqvirtual.edu.co")))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.estado").value(422));
    }

    @Test
    void estrellasFueraDeRangoResponde400() throws Exception {
        mockMvc.perform(post("/api/transacciones/TRX-001/calificacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estrellas\": 7}")
                        .with(usuario("luis@uqvirtual.edu.co")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400));
    }

    @Test
    void quienNoParticipoRecibe422() throws Exception {
        mockMvc.perform(post("/api/transacciones/TRX-001/calificacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estrellas\": 5}")
                        .with(usuario("sofia@uqvirtual.edu.co")))
                .andExpect(status().is(422));
    }

    @Test
    void unaTransaccionPendienteNoSePuedeCalificar() throws Exception {
        mockMvc.perform(post("/api/transacciones/TRX-002/calificacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estrellas\": 4}")
                        .with(usuario("carlos@uniquindio.edu.co")))
                .andExpect(status().is(422));
    }

    @Test
    void sinTokenResponde401() throws Exception {
        mockMvc.perform(post("/api/transacciones/TRX-001/calificacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estrellas\": 5}"))
                .andExpect(status().isUnauthorized());
    }
}