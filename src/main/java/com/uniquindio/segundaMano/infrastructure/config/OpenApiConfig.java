package com.uniquindio.segundaMano.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Documentación Swagger (OpenAPI). Declara el esquema "bearerAuth" (JWT en la cabecera Authorization)
 * y lo exige en toda la API: por eso Swagger UI muestra el botón Authorize.
 * Flujo: POST /api/auth/login -> copiar el token -> Authorize -> pegar el token (sin escribir "Bearer").
 */
@Configuration
public class OpenApiConfig {

    private static final String ESQUEMA = "bearerAuth";

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("UniSegundaMano API")
                        .version("1.0")
                        .description("Marketplace de segunda mano para la comunidad de la Universidad del Quindío. "
                                + "Inicie sesión en /api/auth/login y use el botón Authorize con el token recibido."))
                .components(new Components().addSecuritySchemes(ESQUEMA, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA));
    }
}
