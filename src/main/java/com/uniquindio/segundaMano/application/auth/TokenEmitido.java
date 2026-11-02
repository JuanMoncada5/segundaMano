package com.uniquindio.segundaMano.application.auth;

import java.time.Instant;

public record TokenEmitido(String valor, Instant expiraEn) {
}
