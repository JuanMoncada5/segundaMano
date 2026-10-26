package com.uniquindio.segundaMano.infrastructure.config;

import com.uniquindio.segundaMano.application.usecase.AceptarOfertaUseCase;
import com.uniquindio.segundaMano.application.usecase.AsignarEncuentroSeguroUseCase;
import com.uniquindio.segundaMano.application.usecase.ConfirmarTransaccionUseCase;
import com.uniquindio.segundaMano.application.usecase.ConsultarTransaccionUseCase;
import com.uniquindio.segundaMano.application.usecase.ExpirarPublicacionesUseCase;
import com.uniquindio.segundaMano.application.usecase.ListarPublicacionesActivasUseCase;
import com.uniquindio.segundaMano.application.usecase.ListarTransaccionesDeUniCambistaUseCase;
import com.uniquindio.segundaMano.application.usecase.OfertarArticuloUseCase;
import com.uniquindio.segundaMano.application.usecase.PublicarArticuloUseCase;
import com.uniquindio.segundaMano.application.usecase.RegistrarUniCambistaUseCase;
import com.uniquindio.segundaMano.application.usecase.VerificarCorreoUseCase;
import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;
import com.uniquindio.segundaMano.domain.repository.TransaccionRepository;
import com.uniquindio.segundaMano.domain.repository.UniCambistaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.uniquindio.segundaMano.application.usecase.ConsultarPublicacionUseCase;
import com.uniquindio.segundaMano.application.usecase.ConsultarUniCambistaUseCase;

/**
 * Registra los casos de uso como beans de Spring.
 * Los casos de uso son clases Java normales en infrastructure, se les entregan los repositories.
 * Spring inyecta la implementación activa de cada interfaz, es decir los RepositoryJpa.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public RegistrarUniCambistaUseCase registrarUniCambistaUseCase(UniCambistaRepository uniCambistas) {
        return new RegistrarUniCambistaUseCase(uniCambistas);
    }

    @Bean
    public VerificarCorreoUseCase verificarCorreoUseCase(UniCambistaRepository uniCambistas) {
        return new VerificarCorreoUseCase(uniCambistas);
    }

    @Bean
    public PublicarArticuloUseCase publicarArticuloUseCase(PublicacionRepository publicaciones,
                                                           UniCambistaRepository uniCambistas) {
        return new PublicarArticuloUseCase(publicaciones, uniCambistas);
    }

    @Bean
    public OfertarArticuloUseCase ofertarArticuloUseCase(PublicacionRepository publicaciones,
                                                         UniCambistaRepository uniCambistas) {
        return new OfertarArticuloUseCase(publicaciones, uniCambistas);
    }

    @Bean
    public AceptarOfertaUseCase aceptarOfertaUseCase(PublicacionRepository publicaciones,
                                                     TransaccionRepository transacciones,
                                                     UniCambistaRepository uniCambistas) {
        return new AceptarOfertaUseCase(publicaciones, transacciones, uniCambistas);
    }

    @Bean
    public AsignarEncuentroSeguroUseCase asignarEncuentroSeguroUseCase(TransaccionRepository transacciones,
                                                                       UniCambistaRepository uniCambistas) {
        return new AsignarEncuentroSeguroUseCase(transacciones, uniCambistas);
    }

    @Bean
    public ConfirmarTransaccionUseCase confirmarTransaccionUseCase(TransaccionRepository transacciones,
                                                                   UniCambistaRepository uniCambistas) {
        return new ConfirmarTransaccionUseCase(transacciones, uniCambistas);
    }

    @Bean
    public ExpirarPublicacionesUseCase expirarPublicacionesUseCase(PublicacionRepository publicaciones) {
        return new ExpirarPublicacionesUseCase(publicaciones);
    }

    @Bean
    public ListarPublicacionesActivasUseCase listarPublicacionesActivasUseCase(PublicacionRepository publicaciones) {
        return new ListarPublicacionesActivasUseCase(publicaciones);
    }

    @Bean
    public ConsultarTransaccionUseCase consultarTransaccionUseCase(TransaccionRepository transacciones) {
        return new ConsultarTransaccionUseCase(transacciones);
    }

    @Bean
    public ListarTransaccionesDeUniCambistaUseCase listarTransaccionesDeUniCambistaUseCase(
            TransaccionRepository transacciones, UniCambistaRepository uniCambistas) {
        return new ListarTransaccionesDeUniCambistaUseCase(transacciones, uniCambistas);
    }
    @Bean
    public ConsultarPublicacionUseCase consultarPublicacionUseCase(PublicacionRepository publicaciones) {
        return new ConsultarPublicacionUseCase(publicaciones);
    }

    @Bean
    public ConsultarUniCambistaUseCase consultarUniCambistaUseCase(UniCambistaRepository uniCambistas) {
        return new ConsultarUniCambistaUseCase(uniCambistas);
    }
}
