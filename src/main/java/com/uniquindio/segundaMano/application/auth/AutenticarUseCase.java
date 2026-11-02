package com.uniquindio.segundaMano.application.auth;

import com.uniquindio.segundaMano.domain.entity.UniCambista;

/** Inicio de sesión: verifica la contraseña y entrega un token firmado. */
public class AutenticarUseCase {

    private final CredencialRepository credencialRepository;
    private final CodificadorContrasena codificador;
    private final EmisorToken emisorToken;

    public AutenticarUseCase(CredencialRepository credencialRepository,
                             CodificadorContrasena codificador,
                             EmisorToken emisorToken) {
        this.credencialRepository = credencialRepository;
        this.codificador = codificador;
        this.emisorToken = emisorToken;
    }

    public LoginResponse ejecutar(LoginRequest request) {
        if (request.correoInstitucional() == null || request.contrasena() == null) {
            throw new CredencialesInvalidasException();
        }
        String correo = UniCambista.normalizarCorreo(request.correoInstitucional());
        Credencial credencial = credencialRepository.buscarPorCorreo(correo)
                .orElseThrow(CredencialesInvalidasException::new);
        if (!codificador.coincide(request.contrasena(), credencial.contrasenaHash())) {
            throw new CredencialesInvalidasException();
        }
        TokenEmitido token = emisorToken.emitir(credencial.correoInstitucional(), credencial.rol());
        return new LoginResponse(token.valor(), "Bearer", token.expiraEn().toString(),
                credencial.correoInstitucional(), credencial.rol().name());
    }
}
