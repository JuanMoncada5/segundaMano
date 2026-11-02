package com.uniquindio.segundaMano.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Los errores de seguridad ocurren en un filtro, ANTES de llegar a los controllers, por eso el
 * ManejadorGlobalDeExcepciones no los ve. Aquí se responden con el mismo formato JSON del resto de la API.
 */
@Component
public class RespuestasSeguridadJson implements AuthenticationEntryPoint, AccessDeniedHandler {

    /** Sin token, token vencido o con firma inválida: 401. */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException excepcion) throws IOException {
        escribir(response, 401, "No autenticado", "Falta el token o es inválido o está vencido");
    }

    /** Token válido pero el rol no alcanza: 403. */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException excepcion) throws IOException {
        escribir(response, 403, "Acceso denegado", "Tu rol no tiene permiso para esta ruta");
    }

    private void escribir(HttpServletResponse response, int estado, String error, String mensaje) throws IOException {
        response.setStatus(estado);
        response.setContentType("application/json;charset=UTF-8");
        String json = "{\"estado\":" + estado + ",\"error\":\"" + error + "\",\"mensaje\":\"" + mensaje + "\",\"detalles\":[]}";
        response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
    }
}
