package com.ProyectoPaginaWeb.ProyectoPagina.controlador;

import com.ProyectoPaginaWeb.ProyectoPagina.config.JwtService;
import com.ProyectoPaginaWeb.ProyectoPagina.config.UsuarioDetails;
import com.ProyectoPaginaWeb.ProyectoPagina.modelo.Credenciales;
import com.ProyectoPaginaWeb.ProyectoPagina.repositorio.CredencialesRepository;
import com.ProyectoPaginaWeb.ProyectoPagina.servicio.UsuarioDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;  // ⬅️ AÑADE ESTA IMPORTACIÓN
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioDetailsService usuarioDetailsService;
    private final CredencialesRepository credencialesRepository;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UsuarioDetailsService usuarioDetailsService,
            CredencialesRepository credencialesRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioDetailsService = usuarioDetailsService;
        this.credencialesRepository = credencialesRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginRequest loginRequest, 
                                        HttpServletResponse response) {
        try {
            // Autenticar con Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );

            // Obtener UserDetails
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // Generar token JWT
            String jwtToken = jwtService.generateToken(userDetails);
            
            // Obtener información adicional del usuario
            Optional<Credenciales> credencialesOpt = credencialesRepository.findByCorreo(loginRequest.getEmail());
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("token", jwtToken);
            responseData.put("message", "Autenticación exitosa");
            responseData.put("timestamp", LocalDateTime.now().toString());
            
            if (credencialesOpt.isPresent() && userDetails instanceof UsuarioDetails usuarioDetails) {
                Map<String, Object> usuarioData = new HashMap<>();
                usuarioData.put("id", usuarioDetails.getUsuario().getIdUsuario());
                usuarioData.put("nombre", usuarioDetails.getUsuario().getNombre());
                usuarioData.put("apellido", usuarioDetails.getUsuario().getApellido());
                usuarioData.put("email", loginRequest.getEmail());
                usuarioData.put("rol", usuarioDetails.getUsuario().getRol().getNombreRol());
                usuarioData.put("rolId", usuarioDetails.getUsuario().getRol().getIdRol());
                
                responseData.put("usuario", usuarioData);
            }
            
            return ResponseEntity.ok(responseData);
            
        } catch (BadCredentialsException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Credenciales inválidas");
            errorResponse.put("message", "El correo o contraseña son incorrectos");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error de autenticación");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token no proporcionado"));
            }
            
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            
            UserDetails userDetails = usuarioDetailsService.loadUserByUsername(username);
            
            if (jwtService.isTokenValid(token, userDetails)) {
                Map<String, Object> response = new HashMap<>();
                response.put("valid", true);
                response.put("username", username);
                
                if (userDetails instanceof UsuarioDetails usuarioDetails) {
                    response.put("usuario", Map.of(
                        "id", usuarioDetails.getUsuario().getIdUsuario(),
                        "nombre", usuarioDetails.getUsuario().getNombre(),
                        "apellido", usuarioDetails.getUsuario().getApellido(),
                        "rol", usuarioDetails.getUsuario().getRol().getNombreRol(),
                        "rolId", usuarioDetails.getUsuario().getRol().getIdRol()
                    ));
                }
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Error al verificar token", "message", e.getMessage()));
        }
    }
    

    /**
     * Endpoint para obtener información del usuario actual
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No autenticado"));
            }
            
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            
            UserDetails userDetails = usuarioDetailsService.loadUserByUsername(username);
            
            if (!jwtService.isTokenValid(token, userDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido"));
            }
            
            if (userDetails instanceof UsuarioDetails usuarioDetails) {
                Map<String, Object> response = new HashMap<>();
                response.put("id", usuarioDetails.getUsuario().getIdUsuario());
                response.put("nombre", usuarioDetails.getUsuario().getNombre());
                response.put("apellido", usuarioDetails.getUsuario().getApellido());
                response.put("email", username);
                response.put("rol", usuarioDetails.getUsuario().getRol().getNombreRol());
                response.put("rolId", usuarioDetails.getUsuario().getRol().getIdRol());
                response.put("dni", usuarioDetails.getUsuario().getDni());
                response.put("telefono", usuarioDetails.getUsuario().getTelefono());
                
                return ResponseEntity.ok(response);
            }
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al obtener información del usuario"));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Error de autenticación", "message", e.getMessage()));
        }
    }
    
    @GetMapping("/verificar-thymeleaf")
    public ResponseEntity<?> verificarAutenticacionThymeleaf(@AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> response = new HashMap<>();
        
        if (userDetails != null && userDetails instanceof UserDetails) {
            response.put("autenticado", true);
            response.put("nombre", userDetails.getUsername());
            
            if (userDetails instanceof UsuarioDetails usuarioDetails) {
                response.put("usuario", Map.of(
                    "id", usuarioDetails.getUsuario().getIdUsuario(),
                    "nombre", usuarioDetails.getUsuario().getNombre(),
                    "apellido", usuarioDetails.getUsuario().getApellido(),
                    "rol", usuarioDetails.getUsuario().getRol().getNombreRol()
                ));
            }
        } else {
            response.put("autenticado", false);
        }
        
        return ResponseEntity.ok(response);
    }
}

// Clase interna para el request
class LoginRequest {
    private String email;
    private String password;

    // Getters y Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}