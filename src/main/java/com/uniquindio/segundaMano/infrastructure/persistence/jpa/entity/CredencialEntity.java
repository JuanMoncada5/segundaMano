package com.uniquindio.segundaMano.infrastructure.persistence.jpa.entity;

import com.uniquindio.segundaMano.application.auth.Rol;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Modelo JPA de la credencial de acceso. Tabla aparte: iniciar sesión no es una regla del dominio del marketplace. */
@Entity
@Table(name = "credenciales")
public class CredencialEntity {

    @Id
    private String correoInstitucional;

    private String contrasenaHash;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    /** JPA exige un constructor vacío. */
    protected CredencialEntity() {
    }

    public CredencialEntity(String correoInstitucional, String contrasenaHash, Rol rol) {
        this.correoInstitucional = correoInstitucional;
        this.contrasenaHash = contrasenaHash;
        this.rol = rol;
    }

    public String getCorreoInstitucional() {
        return correoInstitucional;
    }

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public Rol getRol() {
        return rol;
    }
}
