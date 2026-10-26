package com.uniquindio.segundaMano.infrastructure.persistence.jpa;

import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.repository.UniCambistaRepository;
import com.uniquindio.segundaMano.domain.valueObject.RachaConfianza;
import com.uniquindio.segundaMano.infrastructure.persistence.jpa.mapper.UniCambistaPersistenceMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Punto 2: la misma idea que la prueba con @SpringBootTest, pero con @DataJpaTest.
 * @DataJpaTest solo levanta JPA (entidades + interfaces JpaRepository), NO nuestros adaptadores,
 * por eso se traen con @Import el adaptador y su mapper. Cada prueba se deshace sola (es transaccional).
 */
@DataJpaTest
@Import({UniCambistaRepositoryJpa.class, UniCambistaPersistenceMapper.class})
class UniCambistaRepositoryDataJpaTest {

    @Autowired
    private UniCambistaRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void deberiaGuardarYRecuperarUnUniCambistaConSuRachaConfianza() {
        // Arrange
        UniCambista guardado = UniCambista.reconstruir(
                "datajpa@uqvirtual.edu.co", "Daniela", true, new RachaConfianza(2.8, 6));

        // Act
        repository.guardar(guardado);
        entityManager.flush();
        entityManager.clear();
        UniCambista recuperado = repository.buscarPorCorreo("  DataJpa@UQvirtual.edu.co ").orElseThrow();

        // Assert
        assertEquals("datajpa@uqvirtual.edu.co", recuperado.getCorreoInstitucional());
        assertEquals("Daniela", recuperado.getNombre());
        assertTrue(recuperado.isVerificado());
        assertEquals(new RachaConfianza(2.8, 6), recuperado.getRachaConfianza());
        assertTrue(recuperado.getRachaConfianza().debeMostrarAdvertencia()); // 6 transacciones y < 3.0
    }

    @Test
    void buscarUnCorreoQueNoExisteDeberiaDevolverVacio() {
        // Act & Assert
        assertTrue(repository.buscarPorCorreo("nadie@uqvirtual.edu.co").isEmpty());
    }
}