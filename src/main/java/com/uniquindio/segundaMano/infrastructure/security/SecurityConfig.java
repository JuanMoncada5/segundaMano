package com.uniquindio.segundaMano.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;


/**
 * Seguridad con JWT. Roles del sistema:
 *  - UNICAMBISTA: publica y oferta (/api/**)
 *  - ADMIN: además ejecuta tareas de administración (/api/admin/**)
 * Los roles viajan en el claim "roles" del token y se usan SIN prefijo ROLE_.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   RespuestasSeguridadJson respuestasSeguridad) throws Exception {
        http
                // API sin sesión y sin formularios: el token va en cada petición, CSRF no aplica
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // la consola H2 se muestra dentro de un iframe
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        // registrarse, verificar el correo e iniciar sesión se hacen ANTES de tener token: son públicas
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/unicambistas").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/unicambistas/verificar").permitAll()
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/**").hasAnyAuthority("UNICAMBISTA", "ADMIN")
                        .anyRequest().authenticated())
                // 401 y 403 con cuerpo JSON (los filtros de seguridad responden antes de llegar a los controllers)
                .exceptionHandling(errores -> errores
                        .authenticationEntryPoint(respuestasSeguridad)
                        .accessDeniedHandler(respuestasSeguridad))
                .oauth2ResourceServer(oauth -> oauth
                        .authenticationEntryPoint(respuestasSeguridad)
                        .accessDeniedHandler(respuestasSeguridad)
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    /** Verifica la firma de los tokens con la clave de application.properties (HS256). */
    @Bean
    public JwtDecoder jwtDecoder(@Value("${app.jwt.secret}") String secreto) {
        SecretKey clave = new SecretKeySpec(secreto.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(clave).build();
    }

    /** FIRMA los tokens nuevos (login) con la misma clave con la que se verifican arriba. */
    @Bean
    public JwtEncoder jwtEncoder(@Value("${app.jwt.secret}") String secreto) {
        SecretKey clave = new SecretKeySpec(secreto.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return new NimbusJwtEncoder(new ImmutableSecret<>(clave));
    }

    /** Lee los roles del claim "roles" y los deja tal cual (sin anteponer "SCOPE_" ni "ROLE_"). */
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("roles");
        roles.setAuthorityPrefix("");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(roles);
        return converter;
    }
}