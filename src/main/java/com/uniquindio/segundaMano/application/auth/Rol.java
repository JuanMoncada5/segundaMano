package com.uniquindio.segundaMano.application.auth;

/** Roles de acceso al sistema. Viajan dentro del token JWT (claim "roles"), sin prefijo ROLE_. */
public enum Rol {
    UNICAMBISTA,
    ADMIN
}
