package com.uniquindio.segundaMano.infrastructure.persistence.jpa;

import com.uniquindio.segundaMano.domain.entity.Oferta;
import com.uniquindio.segundaMano.domain.entity.Publicacion;
import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.repository.CriteriosBusquedaPublicacion;
import com.uniquindio.segundaMano.domain.repository.Pagina;
import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;
import com.uniquindio.segundaMano.domain.repository.UniCambistaRepository;
import com.uniquindio.segundaMano.domain.valueObject.EstadoOferta;
import com.uniquindio.segundaMano.domain.valueObject.EstadoPublicacion;
import com.uniquindio.segundaMano.domain.valueObject.VigenciaAcademica;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Punto 0: pruebas del repositorio contra H2 de verdad (no Mockito).
 * Se pide el PUERTO del dominio (PublicacionRepository), no el adaptador JPA.
 * @Transactional deshace al final de cada prueba lo que se guardó.
 */
@SpringBootTest
@Transactional
class PublicacionRepositoryJpaTest {

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Autowired
    private UniCambistaRepository uniCambistaRepository;

    @Autowired
    private EntityManager entityManager;

    private UniCambista registrarVerificado(String correo, String nombre) {
        UniCambista uniCambista = UniCambista.registrar(correo, nombre);
        uniCambista.verificarCorreo();
        uniCambistaRepository.guardar(uniCambista);
        return uniCambista;
    }

    @Test
    void deberiaGuardarYRecuperarUnaPublicacionConSuVigenciaAcademicaYSusOfertas() {
        // Arrange
        UniCambista vendedor = registrarVerificado("prueba.vendedor@uqvirtual.edu.co", "Valeria");
        UniCambista comprador = registrarVerificado("prueba.comprador@uqvirtual.edu.co", "Mateo");
        Publicacion publicacion = Publicacion.publicar(vendedor, "Libro de Física I", new BigDecimal("90000"));
        publicacion.asociarVigenciaAcademica(new VigenciaAcademica("Física I", "2026-2"));
        publicacion.recibirOferta(comprador, new BigDecimal("70000"));

        // Act
        publicacionRepository.guardar(publicacion);
        entityManager.flush();  // fuerza los INSERT en la base de datos
        entityManager.clear();  // olvida lo que está en memoria: la lectura sale de H2 de verdad
        Publicacion recuperada = publicacionRepository.buscarPorCodigo(publicacion.getCodigo()).orElseThrow();

        // Assert
        assertEquals("Libro de Física I", recuperada.getTitulo());
        assertEquals(0, new BigDecimal("90000").compareTo(recuperada.getValor()));
        assertEquals(EstadoPublicacion.ACTIVA, recuperada.getEstado());
        assertEquals(new VigenciaAcademica("Física I", "2026-2"), recuperada.getVigenciaAcademica());
        assertEquals("prueba.vendedor@uqvirtual.edu.co", recuperada.getDueno().getCorreoInstitucional());

        assertEquals(1, recuperada.getOfertas().size());
        Oferta oferta = recuperada.getOfertas().get(0);
        assertEquals(0, new BigDecimal("70000").compareTo(oferta.getMontoPropuesto()));
        assertEquals(EstadoOferta.PENDIENTE, oferta.getEstado());
        assertEquals("prueba.comprador@uqvirtual.edu.co", oferta.getOferente().getCorreoInstitucional());
    }

    @Test
    void elRankingDeberiaOrdenarLasPublicacionesActivasPorCantidadDeOfertas() {
        // Arrange: data.sql trae PUB-001 con 3 ofertas, PUB-003 con 2, PUB-002 con 1,
        // PUB-004 sin ofertas y PUB-005 con 4 pero ELIMINADA.

        // Act
        List<Publicacion> ranking = publicacionRepository.listarMasOfertadas(10);

        // Assert
        List<String> codigos = ranking.stream().map(Publicacion::getCodigo).toList();
        assertEquals(List.of("PUB-001", "PUB-003", "PUB-002"), codigos);
        assertEquals(3, ranking.get(0).getOfertas().size());
    }

    @Test
    void elRankingDeberiaRespetarElLimite() {
        // Act
        List<Publicacion> top2 = publicacionRepository.listarMasOfertadas(2);

        // Assert
        assertEquals(List.of("PUB-001", "PUB-003"), top2.stream().map(Publicacion::getCodigo).toList());
    }
    @Test
    void laBusquedaPaginadaDeberiaFiltrarEnLaBaseDeDatosYDarLosTotales() {
        // Arrange: activas con valor <= 100.000 en data.sql: PUB-003 (40.000), PUB-004 (60.000), PUB-002 (80.000)
        CriteriosBusquedaPublicacion primeraPagina =
                new CriteriosBusquedaPublicacion(null, null, null, new BigDecimal("100000"), 0, 2);
        CriteriosBusquedaPublicacion segundaPagina =
                new CriteriosBusquedaPublicacion(null, null, null, new BigDecimal("100000"), 1, 2);

        // Act
        Pagina<Publicacion> pagina0 = publicacionRepository.buscar(primeraPagina);
        Pagina<Publicacion> pagina1 = publicacionRepository.buscar(segundaPagina);

        // Assert: ordenadas por valor, de 2 en 2
        assertEquals(List.of("PUB-003", "PUB-004"), pagina0.contenido().stream().map(Publicacion::getCodigo).toList());
        assertEquals(List.of("PUB-002"), pagina1.contenido().stream().map(Publicacion::getCodigo).toList());
        assertEquals(3, pagina0.totalElementos());
        assertEquals(2, pagina0.totalPaginas());
    }

    @Test
    void laBusquedaNuncaDevuelveLasEliminadasYFiltraPorTextoYCurso() {
        // Act: PUB-005 "Monitor 24 pulgadas" existe en data.sql pero está ELIMINADA
        Pagina<Publicacion> monitor = publicacionRepository.buscar(
                new CriteriosBusquedaPublicacion("monitor", null, null, null, 0, 10));
        Pagina<Publicacion> calculadora = publicacionRepository.buscar(
                new CriteriosBusquedaPublicacion("CALC", null, null, null, 0, 10));
        Pagina<Publicacion> calculo = publicacionRepository.buscar(
                new CriteriosBusquedaPublicacion(null, "cálculo i", null, null, 0, 10));

        // Assert
        assertEquals(0, monitor.totalElementos());
        assertEquals(List.of("PUB-001"), calculadora.contenido().stream().map(Publicacion::getCodigo).toList());
        assertEquals(List.of("PUB-002"), calculo.contenido().stream().map(Publicacion::getCodigo).toList());
    }
}