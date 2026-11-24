package com.readingapp.reading_app.controller;

import com.readingapp.reading_app.config.SecurityUtils;
import com.readingapp.reading_app.dto.NotificacionDTO;
import com.readingapp.reading_app.model.enums.TipoNotificacion;
import com.readingapp.reading_app.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @GetMapping
    public ResponseEntity<List<NotificacionDTO.Response>> obtenerTodas(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(notificacionService.obtenerTodas(SecurityUtils.getUsuarioAutenticadoId(), pageable));
    }

    @GetMapping("/no-leidas")
    public ResponseEntity<List<NotificacionDTO.Response>> obtenerNoLeidas(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(notificacionService.obtenerNoLeidas(SecurityUtils.getUsuarioAutenticadoId(), pageable));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<NotificacionDTO.Response>> obtenerPorTipo(@PathVariable TipoNotificacion tipo, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(notificacionService.obtenerPorTipo(SecurityUtils.getUsuarioAutenticadoId(), tipo, pageable));
    }

    @GetMapping("/contador")
    public ResponseEntity<NotificacionDTO.ContadorResponse> contarNoLeidas() {
        return ResponseEntity.ok(notificacionService.contarNoLeidas(SecurityUtils.getUsuarioAutenticadoId()));
    }

    @PutMapping("/{id}/leida")
    public ResponseEntity<Void> marcarLeida(@PathVariable Long id) {
        notificacionService.marcarLeida(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/leer-todas")
    public ResponseEntity<Void> marcarTodasLeidas() {
        notificacionService.marcarTodasLeidas(SecurityUtils.getUsuarioAutenticadoId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        notificacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
