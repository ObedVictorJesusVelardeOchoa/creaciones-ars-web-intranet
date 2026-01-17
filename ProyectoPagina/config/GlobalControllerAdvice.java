package com.ProyectoPaginaWeb.ProyectoPagina.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void agregarAtributosGlobales(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && 
            authentication.getPrincipal() instanceof UserDetails) {
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // Usuario autenticado
            model.addAttribute("usuarioAutenticado", true);
            model.addAttribute("nombreUsuario", userDetails.getUsername());
            
            // Verificar roles
            boolean esAdministrador = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_Administrador"));
            model.addAttribute("esAdministrador", esAdministrador);
            
        } else {
            // Usuario no autenticado
            model.addAttribute("usuarioAutenticado", false);
            model.addAttribute("nombreUsuario", null);
            model.addAttribute("esAdministrador", false);
        }
    }
}