package com.readingapp.reading_app.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BorradoCuentaRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private static final String SUS_RESENAS = "(SELECT idresena FROM resena WHERE idusuario = :id)";
    private static final String SUS_RECOMENDACIONES =
            "(SELECT idrecomendacion FROM recomendacion WHERE idusuario_emisor = :id OR idusuario_receptor = :id)";
    private static final String SUS_RETOS = "(SELECT idreto FROM reto WHERE id_creador = :id)";

    private static final List<String> SENTENCIAS = List.of(
            "DELETE FROM notificacion WHERE idusuario = :id OR idusuario_origen = :id " +
                    "OR idresena IN " + SUS_RESENAS + " OR idrecomendacion IN " + SUS_RECOMENDACIONES +
                    " OR idreto IN " + SUS_RETOS +
                    " OR idanotacion IN (SELECT idanotacion FROM anotacion WHERE idusuario = :id)" +
                    " OR idlista IN (SELECT idlista FROM lista WHERE idusuario = :id)",
            "DELETE FROM likea WHERE idusuario = :id OR idresena IN " + SUS_RESENAS,
            "DELETE FROM resena_etiqueta WHERE idresena IN " + SUS_RESENAS,
            "DELETE FROM resena WHERE idusuario = :id",
            "DELETE FROM recomendacion WHERE idusuario_emisor = :id OR idusuario_receptor = :id",
            "DELETE FROM valoracion WHERE idusuario = :id",
            "DELETE FROM participante_reto WHERE id_usuario = :id OR id_reto IN " + SUS_RETOS,
            "DELETE FROM reto WHERE id_creador = :id",
            "DELETE FROM like_lista WHERE idusuario = :id OR idlista IN (SELECT idlista FROM lista WHERE idusuario = :id)",
            "DELETE FROM lista_libro WHERE idlista IN (SELECT idlista FROM lista WHERE idusuario = :id)",
            "DELETE FROM lista WHERE idusuario = :id",
            "DELETE FROM cita_guardada WHERE idusuario = :id " +
                    "OR idanotacion IN (SELECT idanotacion FROM anotacion WHERE idusuario = :id)",
            "DELETE FROM like_cita WHERE idusuario = :id " +
                    "OR idanotacion IN (SELECT idanotacion FROM anotacion WHERE idusuario = :id)",
            "DELETE FROM anotacion WHERE idusuario = :id",
            "DELETE FROM favorito WHERE idusuario = :id",
            "DELETE FROM seguimiento WHERE idusuario = :id",
            "DELETE FROM sesion_lectura WHERE id_usuario = :id",
            "UPDATE autor SET seguidores = GREATEST(seguidores - 1, 0) " +
                    "WHERE idautor IN (SELECT idautor FROM sigue_autor WHERE idusuario = :id)",
            "DELETE FROM sigue_autor WHERE idusuario = :id",
            "DELETE FROM sigue WHERE idusuario_seguidor = :id OR idusuario_seguido = :id",
            "DELETE FROM usuario_dia_protegido WHERE idusuario = :id",
            "DELETE FROM usuario WHERE idusuario = :id"
    );

    public void borrarCuenta(Long idusuario) {
        entityManager.flush();
        for (String sql : SENTENCIAS) {
            entityManager.createNativeQuery(sql).setParameter("id", idusuario).executeUpdate();
        }
        entityManager.clear();
    }
}
