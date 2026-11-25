package com.readingapp.reading_app.repository;

import com.readingapp.reading_app.model.LikeCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface LikeCitaRepository extends JpaRepository<LikeCita, Long> {

    boolean existsByUsuarioIdusuarioAndAnotacionIdanotacion(Long idusuario, Long idanotacion);

    @Modifying
    @Query("DELETE FROM LikeCita l WHERE l.usuario.idusuario = :idusuario AND l.anotacion.idanotacion = :idanotacion")
    void quitar(@Param("idusuario") Long idusuario, @Param("idanotacion") Long idanotacion);

    @Modifying
    @Query("DELETE FROM LikeCita l WHERE l.anotacion.idanotacion = :idanotacion")
    void borrarDeAnotacion(@Param("idanotacion") Long idanotacion);

    @Query("SELECT l.anotacion.idanotacion, COUNT(l) FROM LikeCita l WHERE l.anotacion.idanotacion IN :ids " +
            "GROUP BY l.anotacion.idanotacion")
    List<Object[]> contarPorCita(@Param("ids") Collection<Long> ids);

    @Query("SELECT l.anotacion.idanotacion FROM LikeCita l WHERE l.usuario.idusuario = :idusuario " +
            "AND l.anotacion.idanotacion IN :ids")
    List<Long> findIdsLikeadas(@Param("idusuario") Long idusuario, @Param("ids") Collection<Long> ids);
}
