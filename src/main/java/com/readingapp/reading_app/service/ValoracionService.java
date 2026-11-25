package com.readingapp.reading_app.service;

import com.readingapp.reading_app.config.SecurityUtils;
import com.readingapp.reading_app.dto.ValoracionDTO;
import com.readingapp.reading_app.model.Libro;
import com.readingapp.reading_app.model.Seguimiento;
import com.readingapp.reading_app.model.Usuario;
import com.readingapp.reading_app.model.Valoracion;
import com.readingapp.reading_app.model.enums.EstadoLectura;
import com.readingapp.reading_app.repository.LibroRepository;
import com.readingapp.reading_app.repository.ResenaRepository;
import com.readingapp.reading_app.repository.SeguimientoRepository;
import com.readingapp.reading_app.repository.UsuarioRepository;
import com.readingapp.reading_app.repository.ValoracionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ValoracionService {

    private final ValoracionRepository valoracionRepository;
    private final ResenaRepository resenaRepository;
    private final SeguimientoRepository seguimientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LibroRepository libroRepository;

    @Transactional(readOnly = true)
    public Optional<ValoracionDTO.Response> obtenerMia(Long idlibro) {
        return valoracionRepository.findByUsuarioIdusuarioAndLibroIdlibro(SecurityUtils.getUsuarioAutenticadoId(), idlibro)
                .map(this::toResponse);
    }

    @Transactional
    public ValoracionDTO.Response valorar(Long idlibro, BigDecimal puntuacion) {
        if (puntuacion == null || puntuacion.remainder(new BigDecimal("0.5")).signum() != 0) {
            throw new IllegalArgumentException("La puntuación debe ir de 0,5 en 0,5");
        }
        Long idusuario = SecurityUtils.getUsuarioAutenticadoId();
        Usuario usuario = usuarioRepository.findById(idusuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        Libro libro = libroRepository.findById(idlibro)
                .orElseThrow(() -> new EntityNotFoundException("Libro no encontrado"));

        if (resenaRepository.countByUsuarioIdusuarioAndLibroIdlibro(idusuario, idlibro) > 0) {
            throw new IllegalArgumentException("Ya puntuaste este libro en tu reseña: edítala para cambiar la nota");
        }

        Optional<Seguimiento> ultimo = seguimientoRepository
                .findTopByUsuarioIdusuarioAndLibroIdlibroOrderByIdseguimientoDesc(idusuario, idlibro);
        EstadoLectura estado = ultimo.map(Seguimiento::getEstado).orElse(null);
        if (estado == EstadoLectura.LEYENDO) {
            throw new IllegalArgumentException("Lo estás leyendo: podrás puntuarlo cuando lo termines");
        }
        if (estado != EstadoLectura.LEIDO) {
            seguimientoRepository.save(Seguimiento.builder().usuario(usuario).libro(libro)
                    .estado(EstadoLectura.LEIDO).numPagina(0).fecha(LocalDate.now()).lecturaAntigua(true).build());
        }

        Valoracion valoracion = valoracionRepository.findByUsuarioIdusuarioAndLibroIdlibro(idusuario, idlibro)
                .orElseGet(() -> Valoracion.builder().usuario(usuario).libro(libro).build());
        valoracion.setPuntuacion(puntuacion);
        valoracion.setFecha(LocalDateTime.now());
        return toResponse(valoracionRepository.save(valoracion));
    }

    @Transactional
    public void quitar(Long idlibro) {
        Long idusuario = SecurityUtils.getUsuarioAutenticadoId();
        valoracionRepository.borrar(idusuario, idlibro);
        seguimientoRepository.findTopByUsuarioIdusuarioAndLibroIdlibroOrderByIdseguimientoDesc(idusuario, idlibro)
                .filter(s -> Boolean.TRUE.equals(s.getLecturaAntigua()))
                .ifPresent(seguimientoRepository::delete);
    }

    private ValoracionDTO.Response toResponse(Valoracion v) {
        return ValoracionDTO.Response.builder()
                .idlibro(v.getLibro().getIdlibro())
                .puntuacion(v.getPuntuacion())
                .fecha(v.getFecha() != null ? v.getFecha().toString() : null)
                .build();
    }
}
