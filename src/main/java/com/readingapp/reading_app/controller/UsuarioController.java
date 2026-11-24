package com.readingapp.reading_app.controller;

import com.readingapp.reading_app.config.SecurityUtils;
import com.readingapp.reading_app.dto.UsuarioDTO;
import com.readingapp.reading_app.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    /*dos peticiones similares, la primera para consultas rapidas y la segunda para la pagina del perfil con estadisticas completas*/
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO.Response> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @GetMapping("/{id}/perfil")
    public ResponseEntity<UsuarioDTO.PerfilResponse> obtenerPerfil(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPerfil(id));
    }

    @PutMapping("/yo")
    public ResponseEntity<UsuarioDTO.Response> actualizar(@RequestBody UsuarioDTO.UpdateRequest request) {
        return ResponseEntity.ok(usuarioService.actualizar(SecurityUtils.getUsuarioAutenticadoId(), request));
    }

    /*Borrado de una cuenta por un administrador.*/
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        SecurityUtils.validarAdmin();
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/yo/password")
    public ResponseEntity<Void> cambiarPassword(@Valid @RequestBody UsuarioDTO.CambiarPasswordRequest request) {
        usuarioService.cambiarPassword(SecurityUtils.getUsuarioAutenticadoId(), request.getActual(), request.getNueva());
        return ResponseEntity.noContent().build();
    }

    /*El propio usuario se da de baja confirmando su contraseña.*/
    @PostMapping("/yo/eliminar-cuenta")
    public ResponseEntity<Void> eliminarCuentaPropia(@Valid @RequestBody UsuarioDTO.EliminarCuentaRequest request) {
        usuarioService.eliminarCuentaPropia(SecurityUtils.getUsuarioAutenticadoId(), request.getPassword());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<UsuarioDTO.Response>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(usuarioService.buscarPorNombre(nombre));
    }

    @PostMapping("/{seguidoId}/seguir")
    public ResponseEntity<Void> seguirUsuario(@PathVariable Long seguidoId) {
        usuarioService.seguirUsuario(SecurityUtils.getUsuarioAutenticadoId(), seguidoId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{seguidoId}/seguir")
    public ResponseEntity<Void> dejarDeSeguir(@PathVariable Long seguidoId) {
        usuarioService.dejarDeSeguirUsuario(SecurityUtils.getUsuarioAutenticadoId(), seguidoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/seguidos")
    public ResponseEntity<List<UsuarioDTO.Response>> obtenerSeguidos(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerSeguidos(id));
    }

    @GetMapping("/{id}/seguidores")
    public ResponseEntity<List<UsuarioDTO.Response>> obtenerSeguidores(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerSeguidores(id));
    }
}
