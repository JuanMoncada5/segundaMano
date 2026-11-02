package com.uniquindio.segundaMano.application.usecase;

import com.uniquindio.segundaMano.application.dto.PublicacionRankingResponse;
import com.uniquindio.segundaMano.domain.entity.Publicacion;
import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;
import com.uniquindio.segundaMano.infrastructure.persistence.PublicacionRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConsultarRankingPublicacionesUseCaseTest {

    private ConsultarRankingPublicacionesUseCase ranking;

    @BeforeEach
    void preparar() {
        PublicacionRepository publicaciones = new PublicacionRepositoryEnMemoria();
        ranking = new ConsultarRankingPublicacionesUseCase(publicaciones);
        UniCambista vendedora = verificado("ana@uqvirtual.edu.co", "Ana");
        UniCambista luis = verificado("luis@uqvirtual.edu.co", "Luis");
        UniCambista sofia = verificado("sofia@uqvirtual.edu.co", "Sofía");

        Publicacion calculadora = Publicacion.publicar(vendedora, "Calculadora", new BigDecimal("100000"));
        calculadora.recibirOferta(luis, new BigDecimal("60000"));
        calculadora.recibirOferta(sofia, new BigDecimal("70000"));
        Publicacion libro = Publicacion.publicar(vendedora, "Libro", new BigDecimal("50000"));
        libro.recibirOferta(luis, new BigDecimal("30000"));
        Publicacion silla = Publicacion.publicar(vendedora, "Silla", new BigDecimal("40000")); // sin ofertas
        publicaciones.guardar(calculadora);
        publicaciones.guardar(libro);
        publicaciones.guardar(silla);
    }

    private UniCambista verificado(String correo, String nombre) {
        UniCambista u = UniCambista.registrar(correo, nombre);
        u.verificarCorreo();
        return u;
    }

    @Test
    void deberiaOrdenarDeMayorAMenorCantidadDeOfertasConSuPosicion() {
        // Act
        List<PublicacionRankingResponse> resultado = ranking.ejecutar(10);

        // Assert: la silla no tiene ofertas, no aparece
        assertEquals(2, resultado.size());
        assertEquals("Calculadora", resultado.get(0).titulo());
        assertEquals(2, resultado.get(0).cantidadOfertas());
        assertEquals(1, resultado.get(0).posicion());
        assertEquals("Libro", resultado.get(1).titulo());
        assertEquals(2, resultado.get(1).posicion());
    }

    @Test
    void unLimiteFueraDeRangoEsUnDatoInvalido() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> ranking.ejecutar(0));
        assertThrows(IllegalArgumentException.class, () -> ranking.ejecutar(51));
    }
}