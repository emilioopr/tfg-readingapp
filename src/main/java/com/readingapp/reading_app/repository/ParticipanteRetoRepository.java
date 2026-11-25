package com.readingapp.reading_app.repository;

import com.readingapp.reading_app.model.ParticipanteReto;
import com.readingapp.reading_app.model.enums.ModalidadReto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ParticipanteRetoRepository extends JpaRepository<ParticipanteReto, Long> {

    Optional<ParticipanteReto> findByRetoIdretoAndUsuarioIdusuario(Long idreto, Long idusuario);

    boolean existsByRetoIdretoAndUsuarioIdusuario(Long idreto, Long idusuario);

    Page<ParticipanteReto> findByUsuarioIdusuarioAndRetoCumplidoTrue(Long idusuario, Pageable pageable);

    List<ParticipanteReto> findByRetoIdreto(Long idreto);

    @Query("SELECT p FROM ParticipanteReto p WHERE p.retoCumplido = true AND p.fechaCumplimiento >= :desde " +
            "AND p.usuario IN (SELECT seg FROM Usuario u JOIN u.seguidos seg WHERE u.idusuario = :idusuario) " +
            "ORDER BY p.fechaCumplimiento DESC")
    List<ParticipanteReto> findRetosCumplidosDeSeguidos(@Param("idusuario") Long idusuario,
                                                        @Param("desde") java.time.LocalDateTime desde, Pageable pageable);

    @Query("SELECT p FROM ParticipanteReto p WHERE p.usuario.idusuario = :idusuario " +
            "AND p.retoCumplido = false AND p.reto.fechaFin >= :hoy")
    List<ParticipanteReto> findActivosDeUsuario(@Param("idusuario") Long idusuario, @Param("hoy") LocalDate hoy);

    @Query("SELECT p FROM ParticipanteReto p WHERE p.usuario.idusuario = :idusuario " +
            "AND p.retoCumplido = false AND p.reto.fechaFin >= :hoy")
    Page<ParticipanteReto> findActivosDeUsuario(@Param("idusuario") Long idusuario, @Param("hoy") LocalDate hoy, Pageable pageable);

    @Query("SELECT COALESCE(SUM(p.progreso), 0) FROM ParticipanteReto p WHERE p.reto.idreto = :idreto")
    Integer sumProgresoByReto(@Param("idreto") Long idreto);

    @Query("SELECT p FROM ParticipanteReto p WHERE p.reto.idreto = :idreto AND p.retoCumplido = false")
    List<ParticipanteReto> findActivosByReto(@Param("idreto") Long idreto);
}

