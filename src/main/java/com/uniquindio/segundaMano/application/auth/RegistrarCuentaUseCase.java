package com.uniquindio.segundaMano.application.auth;

import com.uniquindio.segundaMano.application.dto.RegistrarUniCambistaRequest;
import com.uniquindio.segundaMano.application.dto.UniCambistaResponse;
import com.uniquindio.segundaMano.application.usecase.RegistrarUniCambistaUseCase;
import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;

/**
 * Registro de una cuenta: primero crea el UniCambista (que valida el correo institucional y que no exista)
 * y solo si eso sale bien guarda la credencial con la contraseña ya codificada.
 */
public class RegistrarCuentaUseCase {

    private static final int LARGO_MINIMO = 8;
    private static final int LARGO_MAXIMO = 72; // límite real de BCrypt

    private final RegistrarUniCambistaUseCase registrarUniCambista;
    private final CredencialRepository credencialRepository;
    private final CodificadorContrasena codificador;

    public RegistrarCuentaUseCase(RegistrarUniCambistaUseCase registrarUniCambista,
                                  CredencialRepository credencialRepository,
                                  CodificadorContrasena codificador) {
        this.registrarUniCambista = registrarUniCambista;
        this.credencialRepository = credencialRepository;
        this.codificador = codificador;
    }

    public UniCambistaResponse ejecutar(RegistrarCuentaRequest request) {
        String contrasena = request.contrasena();
        if (contrasena == null || contrasena.length() < LARGO_MINIMO || contrasena.length() > LARGO_MAXIMO) {
            throw new ReglaDominioException(
                    "La contraseña debe tener entre " + LARGO_MINIMO + " y " + LARGO_MAXIMO + " caracteres");
        }
        UniCambistaResponse registrado = registrarUniCambista.ejecutar(
                new RegistrarUniCambistaRequest(request.correoInstitucional(), request.nombre()));
        credencialRepository.guardar(new Credencial(
                registrado.correoInstitucional(), codificador.codificar(contrasena), Rol.UNICAMBISTA));
        return registrado;
    }
}
