package com.readingapp.reading_app.service;

import com.readingapp.reading_app.dto.SesionLecturaDTO;
import com.readingapp.reading_app.model.SesionLectura;
import com.readingapp.reading_app.model.Usuario;
import com.readingapp.reading_app.repository.LibroRepository;
import com.readingapp.reading_app.repository.SesionLecturaRepository;
import com.readingapp.reading_app.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SesionLecturaService - registro de sesiones de lectura")
class SesionLecturaServiceTest {

    @Mock private SesionLecturaRepository sesionLecturaRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private LibroRepository libroRepository;
    @Mock private RetoService retoService;

    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 24, 20, 0);
    @Spy private Clock clock = Clock.fixed(AHORA.toInstant(ZoneOffset.UTC), ZoneId.of("UTC"));

    @InjectMocks private SesionLecturaService sesionLecturaService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder().idusuario(1L).nombre("ana").inicioSesionLectura(AHORA.minusHours(13)).build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        lenient().when(sesionLecturaRepository.save(any(SesionLectura.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private SesionLecturaDTO.CreateRequest sesion(int minutos) {
        return SesionLecturaDTO.CreateRequest.builder().duracionMinutos(minutos).build();
    }

    @Test
    @DisplayName("Una sesión de hasta 12 horas se registra y actualiza los retos")
    void sesionValida() {
        assertThatCode(() -> sesionLecturaService.registrar(1L, sesion(SesionLecturaDTO.MAX_MINUTOS_SESION)))
                .doesNotThrowAnyException();
        verify(retoService).recalcularRetosActivosDeUsuario(1L);
    }

    @Test
    @DisplayName("Una sesión de más de 12 horas se rechaza y no cuenta para los retos")
    void sesionDemasiadoLarga() {
        assertThatThrownBy(() -> sesionLecturaService.registrar(1L, sesion(100_000)))
                .isInstanceOf(IllegalArgumentException.class);
        verify(sesionLecturaRepository, never()).save(any());
        verify(retoService, never()).recalcularRetosActivosDeUsuario(any());
    }

    @Test
    @DisplayName("No se pueden registrar más minutos de los que han pasado desde que arrancó el temporizador")
    void minutosLimitadosAlTiempoReal() {
        usuario.setInicioSesionLectura(AHORA.minusMinutes(10));

        SesionLecturaDTO.Response res = sesionLecturaService.registrar(1L, sesion(600));

        assertThat(res.getDuracionMinutos()).isEqualTo(11);
        assertThat(usuario.getInicioSesionLectura()).isNull();
    }

    @Test
    @DisplayName("Sin un temporizador en marcha no se registra la sesión")
    void sinInicio() {
        usuario.setInicioSesionLectura(null);

        assertThatThrownBy(() -> sesionLecturaService.registrar(1L, sesion(30)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("en marcha");
        verify(sesionLecturaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Iniciar marca la hora del servidor")
    void iniciarMarcaHora() {
        sesionLecturaService.iniciar(1L);

        assertThat(usuario.getInicioSesionLectura()).isEqualTo(AHORA);
    }
}
