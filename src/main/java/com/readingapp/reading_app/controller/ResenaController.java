package com.readingapp.reading_app.controller;

import com.readingapp.reading_app.config.SecurityUtils;
import com.readingapp.reading_app.dto.ResenaDTO;
import com.readingapp.reading_app.model.Resena;
import com.readingapp.reading_app.service.ResenaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resenas")
@RequiredArgsConstructor
public class ResenaController {

    private final ResenaService resenaService;

    @PostMapping
    public ResponseEntity<ResenaDTO.Response> crear(@Valid @RequestBody ResenaDTO.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resenaService.crear(SecurityUtils.getUsuarioAutenticadoId(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResenaDTO.Response> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(resenaService.obtenerPorId(id));
    }

    /*Todas tus reseñas, también las privadas.*/
    @GetMapping("/mias")
    public ResponseEntity<Page<ResenaDTO.Response>> obtenerMias(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(resenaService.obtenerPorUsuario(SecurityUtils.getUsuarioAutenticadoId(), pageable));
    }

    @GetMapping("/usuario/{idusuario}/publicas")
    public ResponseEntity<Page<ResenaDTO.Response>> obtenerPublicasPorUsuario(
            @PathVariable Long idusuario, @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(resenaService.obtenerPublicasPorUsuario(idusuario, pageable));
    }

    @GetMapping("/libro/{idlibro}")
    public ResponseEntity<Page<ResenaDTO.Response>> obtenerPorLibro(@PathVariable Long idlibro, @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(resenaService.obtenerPorLibro(idlibro,   pageable));
    }

    @GetMapping("/publicas")
    public ResponseEntity<Page<ResenaDTO.Response>> obtenerPublicas(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(resenaService.obtenerPublicas(pageable));
    }

    @GetMapping("/populares")
    public ResponseEntity<List<ResenaDTO.Response>> populares(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(resenaService.obtenerPopulares(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResenaDTO.Response> actualizar(@PathVariable Long id,
                                                         @RequestBody ResenaDTO.UpdateRequest request) {
        return ResponseEntity.ok(resenaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        resenaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/seguidos/{idusuario}")
    public ResponseEntity<List<ResenaDTO.Response>> obtenerResenasDeSeguidos(
            @PathVariable Long idusuario, @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(resenaService.obtenerResenasDeSeguidos(idusuario, pageable));
    }

    // === LIKES ===

    @PostMapping("/{resenaId}/like")
    public ResponseEntity<Void> darLike(@PathVariable Long resenaId) {
        resenaService.darLike(resenaId, SecurityUtils.getUsuarioAutenticadoId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{resenaId}/like")
    public ResponseEntity<Void> quitarLike(@PathVariable Long resenaId) {
        resenaService.quitarLike(resenaId, SecurityUtils.getUsuarioAutenticadoId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/likes")
    public ResponseEntity<List<ResenaDTO.Response>> obtenerLikes() {
        return ResponseEntity.ok(resenaService.obtenerLikesPorUsuario(SecurityUtils.getUsuarioAutenticadoId()));
    }
}
