package com.readingapp.reading_app.config;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static Long getUsuarioAutenticadoId() {
        return (Long) SecurityContextHolder.getContext()
                .getAuthentication().getCredentials();
    }

    public static Long usuarioActualId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getCredentials() instanceof Long id ? id : null;
    }

    public static boolean esUsuarioActual(Long idusuario) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getCredentials() instanceof Long id && id.equals(idusuario);
    }

    public static boolean esAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public static void validarUsuario(Long idusuario) {
        Long tokenId = getUsuarioAutenticadoId();
        if (!tokenId.equals(idusuario)) {
            throw new AccessDeniedException("No tienes permiso para actuar en nombre de otro usuario");
        }
    }

    public static void validarAdmin() {
        if (!esAdmin()) {
            throw new AccessDeniedException("Se requieren permisos de administrador");
        }
    }

    public static void validarUsuarioOAdmin(Long idusuario) {
        if (!esAdmin()) validarUsuario(idusuario);
    }
}
