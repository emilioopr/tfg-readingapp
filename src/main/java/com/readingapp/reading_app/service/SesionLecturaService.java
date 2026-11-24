package com.readingapp.reading_app.service;

import com.readingapp.reading_app.dto.SesionLecturaDTO;
import com.readingapp.reading_app.model.*;
import com.readingapp.reading_app.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SesionLecturaService {

    private final SesionLecturaRepository sesionLecturaRepository;
    private final UsuarioRepository usuarioRepository;
    private final RetoService retoService;
    private final Clock clock;

    /** Marca el inicio del temporizador. Si había otro sin terminar, se descarta. */
    @Transactional
    public void iniciar(Long idusuario) {
        Usuario usuario = usuarioRepository.findById(idusuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        usuario.setInicioSesionLectura(LocalDateTime.now(clock));
    }

    @Transactional
    public SesionLecturaDTO.Response registrar(Long idusuario, SesionLecturaDTO.CreateRequest request) {
        Usuario usuario = usuarioRepository.findById(idusuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        int minutos = request.getDuracionMinutos() != null ? request.getDuracionMinutos() : 0;
        if (minutos < 1 || minutos > SesionLecturaDTO.MAX_MINUTOS_SESION) {
            throw new IllegalArgumentException("Una sesión de lectura debe durar entre 1 minuto y 12 horas");
        }

        LocalDateTime ahora = LocalDateTime.now(clock);
        if (usuario.getInicioSesionLectura() == null) {
            throw new IllegalArgumentException("No hay ninguna sesión de lectura en marcha");
        }
        // Con pausas el cliente cuenta menos que el tiempo real, pero nunca más (+1 por el redondeo de minutos)
        long transcurridos = Duration.between(usuario.getInicioSesionLectura(), ahora).toMinutes() + 1;
        minutos = (int) Math.min(minutos, transcurridos);
        usuario.setInicioSesionLectura(null);

        SesionLectura sesion = SesionLectura.builder()
                .usuario(usuario)
                .inicio(ahora.minusMinutes(minutos))
                .fin(ahora)
                .duracionMinutos(minutos)
                .build();

        sesion = sesionLecturaRepository.save(sesion);

        // Recalcular retos de HORAS activos
        retoService.recalcularRetosActivosDeUsuario(idusuario);

        return toResponse(sesion);
    }

    public Page<SesionLecturaDTO.Response> obtenerPorUsuario(Long idusuario, Pageable pageable) {
        return sesionLecturaRepository.findByUsuarioIdusuario(idusuario, pageable).map(this::toResponse);
    }

    public SesionLecturaDTO.ResumenResponse obtenerResumen(Long idusuario) {
        int totalMinutos = sesionLecturaRepository.sumMinutosByUsuario(idusuario);
        return SesionLecturaDTO.ResumenResponse.builder()
                .idusuario(idusuario)
                .totalMinutos(totalMinutos)
                .totalHoras(Math.round(totalMinutos / 60.0 * 10.0) / 10.0)
                .build();
    }

    private SesionLecturaDTO.Response toResponse(SesionLectura sesion) {
        return SesionLecturaDTO.Response.builder()
                .idsesion(sesion.getIdsesion())
                .idusuario(sesion.getUsuario().getIdusuario())
                .nombreUsuario(sesion.getUsuario().getNombre())
                .inicio(sesion.getInicio().toString())
                .fin(sesion.getFin() != null ? sesion.getFin().toString() : null)
                .duracionMinutos(sesion.getDuracionMinutos())
                .build();
    }
}

