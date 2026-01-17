package com.ProyectoPaginaWeb.ProyectoPagina.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class UsuarioControllerAdvice {

    @ModelAttribute("nombreUsuario")
    public String getNombreUsuario() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getPrincipal())) {
            
            Object principal = authentication.getPrincipal();
            if (principal instanceof UsuarioDetails) {
                UsuarioDetails usuarioDetails = (UsuarioDetails) principal;
                return usuarioDetails.getNombre();
            }
        }
        return null;
    }
    
    @ModelAttribute("nombreCompletoUsuario")
    public String getNombreCompletoUsuario() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getPrincipal())) {
            
            Object principal = authentication.getPrincipal();
            if (principal instanceof UsuarioDetails) {
                UsuarioDetails usuarioDetails = (UsuarioDetails) principal;
                return usuarioDetails.getNombreCompleto();
            }
        }
        return null;
    }
    
    @ModelAttribute("usuarioAutenticado")
    public boolean isUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && 
               !"anonymousUser".equals(authentication.getPrincipal());
    }
    
    // ✅ NUEVO: Método para verificar si el usuario es administrador
    @ModelAttribute("esAdministrador")
    public boolean isAdministrador() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getPrincipal())) {
            
            Object principal = authentication.getPrincipal();
            if (principal instanceof UsuarioDetails) {
                UsuarioDetails usuarioDetails = (UsuarioDetails) principal;
                // Verificar si el rol es "Administrador"
                return usuarioDetails.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_Administrador"));
            }
        }
        return false;
    }
}