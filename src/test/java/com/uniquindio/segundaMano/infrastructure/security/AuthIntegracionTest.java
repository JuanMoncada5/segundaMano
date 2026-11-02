package com.uniquindio.segundaMano.infrastructure.security;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Login real de punta a punta: usa los usuarios de data-credenciales.sql (contraseña Clave123*)
 * y comprueba que el token que fabrica el login es el mismo que luego acepta la seguridad.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthIntegracionTest {

    private static final String CONTRASENA = "Clave123*";
    private static final String RUTA_ADMIN = "/api/admin/publicaciones/expirar";

    @Autowired
    private MockMvc mockMvc;

    private ResultActions login(String correo, String contrasena) throws Exception {
        return mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "correoInstitucional": "%s", "contrasena": "%s" }
                        """.formatted(correo, contrasena)));
    }

    private String tokenDe(String correo) throws Exception {
        String cuerpo = login(correo, CONTRASENA).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(cuerpo, "$.token");
    }

    @Test
    void elLoginDeUnUsuarioDePruebaDevuelveUnTokenConTresPartes() throws Exception {
        String cuerpo = login("ana@uqvirtual.edu.co", CONTRASENA)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.rol").value("UNICAMBISTA"))
                .andReturn().getResponse().getContentAsString();

        String token = JsonPath.read(cuerpo, "$.token");
        assertEquals(3, token.split("\\.").length); // header.payload.firma
    }

    @Test
    void elTokenDelLoginPermiteEntrarARutasProtegidas() throws Exception {
        String token = tokenDe("ana@uqvirtual.edu.co");

        mockMvc.perform(get("/api/publicaciones").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void unUniCambistaNoPuedeEntrarARutasDeAdminYRecibe403ConJson() throws Exception {
        String token = tokenDe("ana@uqvirtual.edu.co");

        mockMvc.perform(post(RUTA_ADMIN).header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.estado").value(403));
    }

    @Test
    void elAdminPuedeEntrarARutasDeAdmin() throws Exception {
        String token = tokenDe("admin@uniquindio.edu.co");

        mockMvc.perform(post(RUTA_ADMIN).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void sinTokenRecibe401ConJson() throws Exception {
        mockMvc.perform(get("/api/publicaciones"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.estado").value(401));
    }

    @Test
    void unTokenConLaFirmaAlteradaEsRechazado() throws Exception {
        String token = tokenDe("ana@uqvirtual.edu.co");
        String alterado = token.substring(0, token.length() - 2) + (token.endsWith("AA") ? "BB" : "AA");

        mockMvc.perform(get("/api/publicaciones").header("Authorization", "Bearer " + alterado))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unaContrasenaIncorrectaRecibe401ConMensajeClaro() throws Exception {
        login("ana@uqvirtual.edu.co", "mala-clave")
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Correo o contraseña incorrectos"));
    }

    @Test
    void unaCuentaRecienRegistradaPuedeIniciarSesion() throws Exception {
        mockMvc.perform(post("/api/unicambistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "correoInstitucional": "nueva@uqvirtual.edu.co", "nombre": "Nueva", "contrasena": "Clave123*" }
                                """))
                .andExpect(status().isCreated());

        login("nueva@uqvirtual.edu.co", CONTRASENA)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correoInstitucional").value("nueva@uqvirtual.edu.co"));
    }

    @Test
    void registrarseSinContrasenaRecibe400() throws Exception {
        mockMvc.perform(post("/api/unicambistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "correoInstitucional": "nueva@uqvirtual.edu.co", "nombre": "Nueva" }
                                """))
                .andExpect(status().isBadRequest());
    }
}
