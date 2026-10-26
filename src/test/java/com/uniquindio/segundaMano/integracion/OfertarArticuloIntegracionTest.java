package com.uniquindio.segundaMano.integracion;

import com.uniquindio.segundaMano.domain.entity.Publicacion;
import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Punto 3: prueba de integración. Recorre el sistema real sin ningún @MockitoBean:
 * HTTP -> seguridad -> PublicacionController -> OfertarArticuloUseCase -> PublicacionRepositoryJpa -> H2.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OfertarArticuloIntegracionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Test
    void deberiaOfertarPorUnArticuloYQuedarGuardadaEnLaBaseDeDatos() throws Exception {
        // Arrange: en data.sql PUB-004 (silla de Ana, 60.000) no tiene ofertas
        String cuerpo = """
                { "correoOferente": "luis@uqvirtual.edu.co", "montoPropuesto": 45000 }
                """;
        var luis = jwt().authorities(new SimpleGrantedAuthority("UNICAMBISTA"));

        // Act
        mockMvc.perform(post("/api/publicaciones/PUB-004/ofertas")
                        .with(luis)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                // Assert (respuesta HTTP)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.codigoPublicacion").value("PUB-004"))
                .andExpect(jsonPath("$.correoOferente").value("luis@uqvirtual.edu.co"));

        // Assert (base de datos): la oferta de verdad quedó guardada
        Publicacion silla = publicacionRepository.buscarPorCodigo("PUB-004").orElseThrow();
        assertEquals(1, silla.getOfertas().size());
        assertEquals(0, new BigDecimal("45000").compareTo(silla.getOfertas().get(0).getMontoPropuesto()));
    }
}