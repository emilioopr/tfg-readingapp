package com.readingapp.reading_app.controller;

import com.readingapp.reading_app.config.SecurityUtils;
import com.readingapp.reading_app.dto.ActividadDTO;
import com.readingapp.reading_app.service.ActividadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/actividad")
@RequiredArgsConstructor
public class ActividadController {

    private final ActividadService actividadService;

    /*Actividad reciente de las personas que sigue el usuario (solo la suya propia).*/
    @GetMapping("/seguidos")
    public ResponseEntity<ActividadDTO.Pagina> deSeguidos(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(actividadService.obtenerDeSeguidos(SecurityUtils.getUsuarioAutenticadoId(), Math.max(page, 0), Math.min(Math.max(size, 1), 50)));
    }
}
