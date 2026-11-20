package com.readingapp.reading_app.service;

import com.readingapp.reading_app.model.Libro;
import com.readingapp.reading_app.model.Notificacion;
import com.readingapp.reading_app.model.Resena;
import com.readingapp.reading_app.model.Usuario;
import com.readingapp.reading_app.model.enums.TipoNotificacion;
import com.readingapp.reading_app.repository.NotificacionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    @Mock private NotificacionRepository notificacionRepository;
    @InjectMocks private NotificacionService notificacionService;

    private final Usuario autor = Usuario.builder().idusuario(1L).nombre("Autor").build();
    private final Usuario fan = Usuario.builder().idusuario(2L).nombre("Fan").build();
    private final Resena resena = Resena.builder().idresena(10L).usuario(autor)
            .libro(Libro.builder().titulo("Dune").build()).build();

    @Test
    @DisplayName("El primer like a una reseña crea la notificación")
    void primerLikeNotifica() {
        when(notificacionRepository.existsByTipoAndResenaIdresenaAndUsuarioOrigenIdusuario(TipoNotificacion.LIKE_RESENA, 10L, 2L))
                .thenReturn(false);

        notificacionService.crearNotificacionLike(resena, fan);

        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    @DisplayName("Quitar y volver a dar like a una reseña no repite la notificación")
    void likeRepetidoNoNotifica() {
        when(notificacionRepository.existsByTipoAndResenaIdresenaAndUsuarioOrigenIdusuario(TipoNotificacion.LIKE_RESENA, 10L, 2L))
                .thenReturn(true);

        notificacionService.crearNotificacionLike(resena, fan);

        verify(notificacionRepository, never()).save(any());
    }
}
