package com.uniquindio.segundaMano.integracion;

import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;
import com.uniquindio.segundaMano.domain.valueObject.EstadoPublicacion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Endpoints nuevos de la Entrega 2 con la seguridad REAL y los datos de data.sql:
 * búsqueda paginada, ranking (solo ADMIN) y eliminación lógica (quién elimina sale del token).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ConsultasYEliminacionEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PublicacionRepository publicacionRepository;

    private RequestPostProcessor usuario(String correo, String rol) {
        return jwt().jwt(token -> token.subject(correo)).authorities(new SimpleGrantedAuthority(rol));
    }

    @Test
    void laBusquedaPaginadaRespondeContenidoYTotales() throws Exception {
        mockMvc.perform(get("/api/publicaciones/buscar")
                        .param("valorMax", "100000").param("pagina", "0").param("tamano", "2")
                        .with(usuario("luis@uqvirtual.edu.co", "UNICAMBISTA")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenido.length()").value(2))
                .andExpect(jsonPath("$.contenido[0].codigo").value("PUB-003"))
                .andExpect(jsonPath("$.totalElementos").value(3))
                .andExpect(jsonPath("$.totalPaginas").value(2));
    }

    @Test
    void unaPaginaNegativaResponde400() throws Exception {
        mockMvc.perform(get("/api/publicaciones/buscar").param("pagina", "-1")
                        .with(usuario("luis@uqvirtual.edu.co", "UNICAMBISTA")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400));
    }

    @Test
    void elAdminVeElRankingDePublicacionesMasOfertadas() throws Exception {
        mockMvc.perform(get("/api/admin/reportes/publicaciones-mas-ofertadas").param("limite", "2")
                        .with(usuario("admin@uniquindio.edu.co", "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].codigo").value("PUB-001"))
                .andExpect(jsonPath("$[0].cantidadOfertas").value(3))
                .andExpect(jsonPath("$[1].codigo").value("PUB-003"));
    }

    @Test
    void unUniCambistaNoPuedeVerElRanking() throws Exception {
        mockMvc.perform(get("/api/admin/reportes/publicaciones-mas-ofertadas")
                        .with(usuario("ana@uqvirtual.edu.co", "UNICAMBISTA")))
                .andExpect(status().isForbidden());
    }

    @Test
    void laDuenaEliminaSuPublicacionYLaFilaSigueAhiComoEliminada() throws Exception {
        // PUB-004 (silla) es de ana en data.sql
        mockMvc.perform(delete("/api/publicaciones/PUB-004").with(usuario("ana@uqvirtual.edu.co", "UNICAMBISTA")))
                .andExpect(status().isNoContent());

        // Eliminación lógica: la Publicación se sigue encontrando, solo cambió su estado
        assertEquals(EstadoPublicacion.ELIMINADA,
                publicacionRepository.buscarPorCodigo("PUB-004").orElseThrow().getEstado());
    }

    @Test
    void quienNoEsElDuenoRecibe422AlEliminar() throws Exception {
        mockMvc.perform(delete("/api/publicaciones/PUB-004").with(usuario("luis@uqvirtual.edu.co", "UNICAMBISTA")))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.mensaje").value("Solo el dueño puede realizar esta acción sobre la Publicación"));

        assertEquals(EstadoPublicacion.ACTIVA,
                publicacionRepository.buscarPorCodigo("PUB-004").orElseThrow().getEstado());
    }

    @Test
    void eliminarUnaPublicacionQueNoExisteResponde404() throws Exception {
        mockMvc.perform(delete("/api/publicaciones/NO-EXISTE").with(usuario("ana@uqvirtual.edu.co", "UNICAMBISTA")))
                .andExpect(status().isNotFound());
    }
}