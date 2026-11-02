package com.uniquindio.segundaMano.application.usecase;

import com.uniquindio.segundaMano.application.dto.BuscarPublicacionesRequest;
import com.uniquindio.segundaMano.application.dto.PaginaResponse;
import com.uniquindio.segundaMano.application.dto.PublicacionResponse;
import com.uniquindio.segundaMano.domain.entity.Publicacion;
import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;
import com.uniquindio.segundaMano.domain.valueObject.VigenciaAcademica;
import com.uniquindio.segundaMano.infrastructure.persistence.PublicacionRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BuscarPublicacionesUseCaseTest {

    private PublicacionRepository publicaciones;
    private BuscarPublicacionesUseCase buscar;

    @BeforeEach
    void preparar() {
        publicaciones = new PublicacionRepositoryEnMemoria();
        buscar = new BuscarPublicacionesUseCase(publicaciones);
        UniCambista ana = UniCambista.registrar("ana@uqvirtual.edu.co", "Ana");
        ana.verificarCorreo();

        publicaciones.guardar(Publicacion.publicar(ana, "Calculadora Casio", new BigDecimal("150000")));
        Publicacion libro = Publicacion.publicar(ana, "Libro de Física", new BigDecimal("80000"));
        libro.asociarVigenciaAcademica(new VigenciaAcademica("Física I", "2026-2"));
        publicaciones.guardar(libro);
        publicaciones.guardar(Publicacion.publicar(ana, "Bata de laboratorio", new BigDecimal("40000")));
        Publicacion eliminada = Publicacion.publicar(ana, "Calculadora vieja", new BigDecimal("30000"));
        eliminada.eliminarLogicamente(ana);
        publicaciones.guardar(eliminada);
    }

    private List<String> titulos(PaginaResponse<PublicacionResponse> pagina) {
        return pagina.contenido().stream().map(PublicacionResponse::titulo).toList();
    }

    @Test
    void deberiaPaginarOrdenadoPorValorYDevolverLosTotales() {
        // Act
        PaginaResponse<PublicacionResponse> primera = buscar.ejecutar(
                new BuscarPublicacionesRequest(null, null, null, null, 0, 2));

        // Assert: 3 activas (la eliminada no cuenta), de 2 en 2 => 2 páginas
        assertEquals(List.of("Bata de laboratorio", "Libro de Física"), titulos(primera));
        assertEquals(3, primera.totalElementos());
        assertEquals(2, primera.totalPaginas());
    }

    @Test
    void deberiaFiltrarPorTextoYNuncaMostrarLasEliminadas() {
        // Act: hay dos "Calculadora", pero una está ELIMINADA
        PaginaResponse<PublicacionResponse> resultado = buscar.ejecutar(
                new BuscarPublicacionesRequest("CALCULADORA", null, null, null, 0, 10));

        // Assert
        assertEquals(List.of("Calculadora Casio"), titulos(resultado));
        assertEquals(1, resultado.totalElementos());
    }

    @Test
    void deberiaFiltrarPorCursoYRangoDeValor() {
        // Act
        PaginaResponse<PublicacionResponse> porCurso = buscar.ejecutar(
                new BuscarPublicacionesRequest(null, "física i", null, null, 0, 10));
        PaginaResponse<PublicacionResponse> porValor = buscar.ejecutar(
                new BuscarPublicacionesRequest("", null, new BigDecimal("50000"), new BigDecimal("200000"), 0, 10));

        // Assert
        assertEquals(List.of("Libro de Física"), titulos(porCurso));
        assertEquals(List.of("Libro de Física", "Calculadora Casio"), titulos(porValor));
    }

    @Test
    void unaPaginaNegativaOUnTamanoFueraDeRangoSonDatosInvalidos() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> buscar.ejecutar(new BuscarPublicacionesRequest(null, null, null, null, -1, 10)));
        assertThrows(IllegalArgumentException.class,
                () -> buscar.ejecutar(new BuscarPublicacionesRequest(null, null, null, null, 0, 0)));
        assertThrows(IllegalArgumentException.class, () -> buscar.ejecutar(new BuscarPublicacionesRequest(
                null, null, new BigDecimal("90000"), new BigDecimal("10000"), 0, 10)));
    }
}