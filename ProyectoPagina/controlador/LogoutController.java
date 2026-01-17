package com.ProyectoPaginaWeb.ProyectoPagina.controlador;

import com.ProyectoPaginaWeb.ProyectoPagina.config.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LogoutController {

    private final JwtService jwtService;

    public LogoutController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Logout tradicional (GET)
     */
    @GetMapping("/logout")
    public String logoutTraditional(HttpServletRequest request, 
                                    HttpServletResponse response,
                                    Authentication authentication) {
        
        // Invalidar sesión HTTP
        request.getSession().invalidate();
        
        // Limpiar cookies de sesión
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("JSESSIONID")) {
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
        
        // Si hay autenticación, cerrarla
        if (authentication != null && authentication.isAuthenticated()) {
            // Spring Security maneja esto automáticamente
        }
        
        // Redirigir a login con mensaje de logout
        return "redirect:/login?logout=true";
    }

    /**
     * API de logout (POST) para JWT
     */
    @PostMapping("/api/auth/logout")
    @ResponseBody
    public ResponseEntity<?> logoutAPI(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        
        Map<String, Object> responseBody = new HashMap<>();
        
        // Si hay token JWT, podemos registrar su invalidez (en una implementación real podrías usar una blacklist)
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                // Extraer información del token para logging
                String username = jwtService.extractUsername(token);
                System.out.println("🔐 Usuario haciendo logout: " + username);
                
                // Aquí podrías agregar el token a una lista negra (blacklist)
                // jwtBlacklistService.addToBlacklist(token);
                
            } catch (Exception e) {
                // Token ya inválido o expirado, no hay problema
            }
        }
        
        // Invalidar sesión HTTP si existe
        try {
            request.getSession().invalidate();
            
            // Limpiar cookies
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("JSESSIONID")) {
                        cookie.setMaxAge(0);
                        response.addCookie(cookie);
                    }
                }
            }
        } catch (Exception e) {
            // Ignorar errores de sesión inválida
        }
        
        responseBody.put("success", true);
        responseBody.put("message", "Logout exitoso");
        responseBody.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(responseBody);
    }

    /**
     * Verificar si hay sesión activa
     */
    @GetMapping("/api/auth/check")
    @ResponseBody
    public ResponseEntity<?> checkAuthStatus(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                           HttpServletRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        // Verificar si hay sesión HTTP activa
        boolean hasHttpSession = request.getSession(false) != null;
        
        // Verificar si hay token JWT válido
        boolean hasValidJWT = false;
        String username = null;
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                username = jwtService.extractUsername(token);
                hasValidJWT = true;
            } catch (Exception e) {
                // Token inválido
            }
        }
        
        response.put("authenticated", hasHttpSession || hasValidJWT);
        response.put("hasHttpSession", hasHttpSession);
        response.put("hasValidJWT", hasValidJWT);
        
        if (username != null) {
            response.put("username", username);
        }
        
        return ResponseEntity.ok(response);
    }
}