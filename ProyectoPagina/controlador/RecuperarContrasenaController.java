package com.ProyectoPaginaWeb.ProyectoPagina.controlador;

import com.ProyectoPaginaWeb.ProyectoPagina.servicio.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/recuperacion") // ✅ AGREGAR RUTA BASE
public class RecuperarContrasenaController {

    @Autowired
    private UsuarioServicio usuarioServicio;

    // ============================================
    // PÁGINAS HTML
    // ============================================

    /**
     * Muestra la página inicial de recuperación de contraseña
     */
    @GetMapping("/contrasena")
    public String mostrarPaginaRecuperar() {
        return "recuperar-contrasena";
    }

    /**
     * Muestra la página para ingresar el código y nueva contraseña
     */
    @GetMapping("/codigo") // ✅ CAMBIAR RUTA
    public String mostrarPaginaCodigo(@RequestParam(required = false) String correo, 
                                    @RequestParam(required = false) Boolean exito,
                                    Model model) {
        if (correo != null) {
            model.addAttribute("correo", correo);
        }
        if (exito != null && exito) {
            model.addAttribute("mensaje", "Código enviado correctamente a tu correo.");
        }
        return "codigo-recuperacion";
    }

    // ============================================
    // PROCESAMIENTO DE FORMULARIOS (MVC TRADICIONAL)
    // ============================================

    /**
     * Procesa el envío del correo para recuperación
     */
    @PostMapping("/contrasena")
    public String procesarRecuperacion(@RequestParam("correo") String correo,
                                     RedirectAttributes redirectAttributes) {
        try {
            usuarioServicio.enviarCodigoRecuperacion(correo);
            redirectAttributes.addAttribute("correo", correo);
            redirectAttributes.addAttribute("exito", true);
            return "redirect:/recuperacion/codigo"; // ✅ ACTUALIZAR REDIRECT
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/recuperacion/contrasena"; // ✅ ACTUALIZAR REDIRECT
        }
    }

    /**
     * Procesa el cambio de contraseña con el código
     */
    @PostMapping("/cambiar-contrasena")
public String procesarCambioContrasena(@RequestParam String correo,
                                     @RequestParam String codigo,
                                     @RequestParam String nuevaContrasena,
                                     @RequestParam String confirmarContrasena,
                                     RedirectAttributes redirectAttributes) {
    try {
        // 1. Validar que las contraseñas coincidan
        if (!nuevaContrasena.equals(confirmarContrasena)) {
            redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
            redirectAttributes.addAttribute("correo", correo);
            return "redirect:/recuperacion/codigo";
        }

        // 2. Verificar que el código sea válido
        boolean codigoValido = usuarioServicio.verificarCodigo(correo, codigo);
        if (!codigoValido) {
            redirectAttributes.addFlashAttribute("error", "Código inválido o expirado");
            redirectAttributes.addAttribute("correo", correo);
            return "redirect:/recuperacion/codigo";
        }

        // 3. Cambiar contraseña
        usuarioServicio.cambiarContrasena(correo, nuevaContrasena);
        
        // 4. ✅ CORREGIDO: Redirigir a una página de éxito separada
        redirectAttributes.addFlashAttribute("mensajeExito", "Contraseña cambiada exitosamente");
        return "redirect:/recuperacion/exito";

    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        redirectAttributes.addAttribute("correo", correo);
        return "redirect:/recuperacion/codigo";
    }
}

// ✅ AGREGAR: Nueva página de éxito
@GetMapping("/exito")
public String mostrarExito(Model model) {
    return "exito-recuperacion";
}

    // ============================================
    // ENDPOINTS API (PARA AJAX/FETCH)
    // ============================================

    /**
     * Endpoint API para enviar código de recuperación
     */
    @PostMapping("/api/enviar-codigo")
    @ResponseBody
    public ResponseEntity<?> enviarCodigoAPI(@RequestBody Map<String, String> datos) {
        String correo = datos.get("correo");

        try {
            usuarioServicio.enviarCodigoRecuperacion(correo);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Código enviado correctamente",
                "correo", correo
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error al enviar el código: " + e.getMessage()
            ));
        }
    }

    /**
     * Endpoint API para verificar código y cambiar contraseña
     */
    @PostMapping("/api/verificar-codigo")
    @ResponseBody
    public ResponseEntity<?> verificarCodigoYActualizarAPI(@RequestBody Map<String, String> datos) {
        String correo = datos.get("correo");
        String codigo = datos.get("codigo");
        String nuevaContrasena = datos.get("nuevaContrasena");

        try {
            // 1. Verificar código
            boolean codigoValido = usuarioServicio.verificarCodigo(correo, codigo);
            if (!codigoValido) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "El código es inválido o ha expirado."
                ));
            }

            // 2. Cambiar contraseña
            usuarioServicio.cambiarContrasena(correo, nuevaContrasena);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Contraseña actualizada correctamente."
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error al cambiar la contraseña: " + e.getMessage()
            ));
        }
    }

    /**
     * Endpoint solo para verificar código (sin cambiar contraseña)
     */
    @PostMapping("/api/verificar")
    @ResponseBody
    public ResponseEntity<?> verificarCodigoAPI(@RequestBody Map<String, String> datos) {
        String correo = datos.get("correo");
        String codigo = datos.get("codigo");

        try {
            boolean valido = usuarioServicio.verificarCodigo(correo, codigo);
            if (valido) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Código válido"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Código inválido o expirado"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error al verificar el código: " + e.getMessage()
            ));
        }
    }
}