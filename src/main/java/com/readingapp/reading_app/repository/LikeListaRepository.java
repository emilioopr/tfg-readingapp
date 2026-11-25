package com.readingapp.reading_app.repository;

import com.readingapp.reading_app.model.LikeLista;
import com.readingapp.reading_app.model.Lista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface LikeListaRepository extends JpaRepository<LikeLista, Long> {

    boolean existsByUsuarioIdusuarioAndListaIdlista(Long idusuario, Long idlista);

    @Modifying
    @Query("DELETE FROM LikeLista l WHERE l.usuario.idusuario = :idusuario AND l.lista.idlista = :idlista")
    void quitar(@Param("idusuario") Long idusuario, @Param("idlista") Long idlista);

    @Modifying
    @Query("DELETE FROM LikeLista l WHERE l.lista.idlista = :idlista")
    void borrarDeLista(@Param("idlista") Long idlista);

    @Query("SELECT l.lista.idlista, COUNT(l) FROM LikeLista l WHERE l.lista.idlista IN :ids GROUP BY l.lista.idlista")
    List<Object[]> contarPorLista(@Param("ids") Collection<Long> ids);

    @Query("SELECT l.lista.idlista FROM LikeLista l WHERE l.usuario.idusuario = :idusuario AND l.lista.idlista IN :ids")
    List<Long> findIdsLikeadas(@Param("idusuario") Long idusuario, @Param("ids") Collection<Long> ids);

    @Query("SELECT l.lista FROM LikeLista l WHERE l.usuario.idusuario = :idusuario " +
            "AND (l.lista.esPublica = true OR l.lista.usuario.idusuario = :idusuario) ORDER BY l.fecha DESC")
    List<Lista> findListasLikeadas(@Param("idusuario") Long idusuario);
}
