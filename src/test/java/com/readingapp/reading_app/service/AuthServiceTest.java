package com.readingapp.reading_app.service;

import com.readingapp.reading_app.config.DemasiadosIntentosException;
import com.readingapp.reading_app.config.JwtUtil;
import com.readingapp.reading_app.dto.AuthDTO;
import com.readingapp.reading_app.model.Usuario;
import com.readingapp.reading_app.model.enums.RolUsuario;
import com.readingapp.reading_app.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - inicio de sesión")
class AuthServiceTest {

    private static final String EMAIL = "ana@test.com";

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private UsuarioService usuarioService;
    @Mock private IntentosLoginService intentosLoginService;

    @InjectMocks private AuthService authService;

    private static AuthDTO.LoginRequest login(String email, String password) {
        return AuthDTO.LoginRequest.builder().email(email).password(password).build();
    }

    private void existeUsuario() {
        Usuario ana = Usuario.builder().idusuario(1L).nombre("ana").email(EMAIL)
                .password("hash").rol(RolUsuario.USER).build();
        when(usuarioRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(ana));
    }

    @Test
    @DisplayName("Con credenciales correctas devuelve el token y los datos del usuario")
    void loginCorrecto() {
        existeUsuario();
        when(passwordEncoder.matches("secreta", "hash")).thenReturn(true);
        when(jwtUtil.generateToken(1L, EMAIL, "USER")).thenReturn("token");

        AuthDTO.AuthResponse r = authService.login(login(EMAIL, "secreta"));

        assertThat(r.getToken()).isEqualTo("token");
        assertThat(r.getIdusuario()).isEqualTo(1L);
        assertThat(r.getRol()).isEqualTo("USER");
        // un login correcto reinicia el contador de intentos fallidos
        verify(intentosLoginService).registrarExito(EMAIL);
        verify(intentosLoginService, never()).registrarFallo(anyString());
    }

    @Test
    @DisplayName("El email se normaliza (espacios y mayúsculas) antes de buscar el usuario")
    void emailNormalizado() {
        existeUsuario();
        when(passwordEncoder.matches("secreta", "hash")).thenReturn(true);

        authService.login(login("  Ana@Test.COM ", "secreta"));

        verify(intentosLoginService).comprobar(EMAIL);
        verify(usuarioRepository).findByEmailIgnoreCase(EMAIL);
    }

    @Test
    @DisplayName("Con contraseña incorrecta se rechaza y se cuenta el fallo")
    void passwordIncorrecta() {
        existeUsuario();
        when(passwordEncoder.matches("mala", "hash")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(login(EMAIL, "mala")))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Email o contraseña incorrectos");
        verify(intentosLoginService).registrarFallo(EMAIL);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    @DisplayName("Con un email inexistente da el mismo error, sin revelar si la cuenta existe")
    void emailInexistenteMismoMensaje() {
        when(usuarioRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(login(EMAIL, "loquesea")))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Email o contraseña incorrectos");
        verify(intentosLoginService).registrarFallo(EMAIL);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("Si el email está bloqueado no se llega a comprobar la contraseña")
    void emailBloqueado() {
        doThrow(new DemasiadosIntentosException("Demasiados intentos"))
                .when(intentosLoginService).comprobar(EMAIL);

        assertThatThrownBy(() -> authService.login(login(EMAIL, "secreta")))
                .isInstanceOf(DemasiadosIntentosException.class);
        verifyNoInteractions(usuarioRepository, passwordEncoder, jwtUtil);
    }
}
