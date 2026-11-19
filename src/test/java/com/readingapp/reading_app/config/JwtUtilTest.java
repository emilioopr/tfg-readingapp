package com.readingapp.reading_app.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtUtil - generación y validación del token")
class JwtUtilTest {

    private static final String SECRETO = "clave-de-pruebas-de-al-menos-32-bytes-para-hmac";

    private JwtUtil jwtUtil;

    private static JwtUtil crear(String secreto, long expiracionMs) {
        JwtUtil util = new JwtUtil();
        ReflectionTestUtils.setField(util, "secret", secreto);
        ReflectionTestUtils.setField(util, "expiration", expiracionMs);
        return util;
    }

    @BeforeEach
    void setUp() {
        jwtUtil = crear(SECRETO, 60_000);
    }

    @Test
    @DisplayName("Un token recién generado es válido y lleva el id, el email y el rol")
    void tokenValidoConDatosDelUsuario() {
        String token = jwtUtil.generateToken(7L, "ana@test.com", "ADMIN");

        assertThat(jwtUtil.isTokenValid(token)).isTrue();
        assertThat(jwtUtil.extractUserId(token)).isEqualTo(7L);
        assertThat(jwtUtil.extractEmail(token)).isEqualTo("ana@test.com");
        assertThat(jwtUtil.extractRol(token)).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("Un token firmado con otra clave no es válido")
    void tokenConOtraFirmaNoEsValido() {
        String ajeno = crear("otra-clave-distinta-tambien-de-mas-de-32-bytes", 60_000)
                .generateToken(1L, "ana@test.com", "ADMIN");

        assertThat(jwtUtil.isTokenValid(ajeno)).isFalse();
    }

    @Test
    @DisplayName("Un token caducado no es válido")
    void tokenCaducadoNoEsValido() {
        String caducado = crear(SECRETO, -1_000).generateToken(1L, "ana@test.com", "USER");

        assertThat(jwtUtil.isTokenValid(caducado)).isFalse();
    }
}
