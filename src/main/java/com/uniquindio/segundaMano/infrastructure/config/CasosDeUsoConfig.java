package com.uniquindio.segundaMano.infrastructure.config;

import com.uniquindio.segundaMano.application.usecase.AceptarOfertaUseCase;
import com.uniquindio.segundaMano.application.usecase.AsignarEncuentroSeguroUseCase;
import com.uniquindio.segundaMano.application.usecase.ConfirmarTransaccionUseCase;
import com.uniquindio.segundaMano.application.usecase.ExpirarPublicacionesUseCase;
import com.uniquindio.segundaMano.application.usecase.OfertarArticuloUseCase;
import com.uniquindio.segundaMano.application.usecase.PublicarArticuloUseCase;
import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;
import com.uniquindio.segundaMano.domain.repository.TransaccionRepository;
import com.uniquindio.segundaMano.domain.repository.UniCambistaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registra los casos de uso como beans de Spring.
 * Así la capa application sigue sin anotaciones de Spring: aquí (infraestructura) se "enchufan"
 * los repositorios JPA a cada caso de uso.
 */
@Configuration
public class CasosDeUsoConfig {

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
}