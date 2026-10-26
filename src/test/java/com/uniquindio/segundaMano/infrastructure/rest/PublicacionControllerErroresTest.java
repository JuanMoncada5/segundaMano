package com.uniquindio.segundaMano.infrastructure.rest;

import com.uniquindio.segundaMano.domain.entity.Publicacion;
import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;
import com.uniquindio.segundaMano.domain.repository.TransaccionRepository;
import com.uniquindio.segundaMano.domain.repository.UniCambistaRepository;
import com.uniquindio.segundaMano.infrastructure.config.UseCaseConfig;
import com.uniquindio.segundaMano.infrastructure.rest.mapper.PublicacionRestMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Punto 4: casos de error del controller.
 * El caso de uso es el REAL (lo trae UseCaseConfig) y lo que se simula son los repositorios.
 * Así la regla de negocio la rompe el dominio de verdad, y con verify(..., never()) se comprueba
 * que el repositorio nunca llegó a guardar nada.
 * @WebMvcTest no carga clases @Component como el mapper, por eso se trae con @Import.
 * addFilters = false apaga la seguridad: aquí solo importa el controller (la seguridad va en el punto 1).
 */
@WebMvcTest(PublicacionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({UseCaseConfig.class, PublicacionRestMapper.class})
class PublicacionControllerErroresTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PublicacionRepository publicacionRepository;

    @MockitoBean
    private UniCambistaRepository uniCambistaRepository;

    @MockitoBean
    private TransaccionRepository transaccionRepository;

    @Test
    void ofertarSobreUnaPublicacionQueNoExisteDeberiaResponder404YNoGuardarNada() throws Exception {
        // Arrange
        when(publicacionRepository.buscarPorCodigo("NO-EXISTE")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/api/publicaciones/NO-EXISTE/ofertas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "correoOferente": "luis@uqvirtual.edu.co", "montoPropuesto": 50000 }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404));

        verify(publicacionRepository, never()).guardar(any());
    }

    @Test
    void ofertarMenosDelCincuentaPorCientoDeberiaResponder400YNoGuardarNada() throws Exception {
        // Arrange: una Publicación de 150.000 y un comprador verificado
        UniCambista vendedora = UniCambista.registrar("ana@uqvirtual.edu.co", "Ana");
        vendedora.verificarCorreo();
        UniCambista comprador = UniCambista.registrar("luis@uqvirtual.edu.co", "Luis");
        comprador.verificarCorreo();
        Publicacion calculadora = Publicacion.publicar(vendedora, "Calculadora Casio", new BigDecimal("150000"));
        when(publicacionRepository.buscarPorCodigo("PUB-001")).thenReturn(Optional.of(calculadora));
        when(uniCambistaRepository.buscarPorCorreo("luis@uqvirtual.edu.co")).thenReturn(Optional.of(comprador));

        // Act & Assert: 50.000 es el 33% del precio, la regla exige mínimo el 50%
        mockMvc.perform(post("/api/publicaciones/PUB-001/ofertas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "correoOferente": "luis@uqvirtual.edu.co", "montoPropuesto": 50000 }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.mensaje").value("El monto no puede ser menor al 50% del precio original"));

        verify(publicacionRepository, never()).guardar(any());
    }
}