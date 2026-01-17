package com.ProyectoPaginaWeb.ProyectoPagina.controlador;

import com.ProyectoPaginaWeb.ProyectoPagina.modelo.*;
import com.ProyectoPaginaWeb.ProyectoPagina.servicio.UsuarioServicio;
import com.ProyectoPaginaWeb.ProyectoPagina.servicio.DireccionServicio;
import com.ProyectoPaginaWeb.ProyectoPagina.repositorio.CredencialesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class PerfilController {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private DireccionServicio direccionServicio;

    @Autowired
    private CredencialesRepository credencialesRepository;

    @GetMapping("/perfil")
    public String mostrarPerfil(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        Usuario usuario = usuarioServicio.findByEmail(email);

        if (usuario == null) {
            return "redirect:/login";
        }

        // Obtener las credenciales para el usuario usando el repositorio existente
        Optional<Credenciales> credencialesOpt = credencialesRepository.findByIdUsuario(usuario.getIdUsuario());
        Credenciales credenciales = credencialesOpt.orElse(null);
        
        List<Direccion> direcciones = direccionServicio.obtenerDireccionesPorUsuario(usuario);

        model.addAttribute("usuario", usuario);
        model.addAttribute("credenciales", credenciales); // Agregar credenciales al modelo
        model.addAttribute("direcciones", direcciones);
        model.addAttribute("usuarioAutenticado", true);
        model.addAttribute("nombreUsuario", usuario.getNombre());

        return "perfil";
    }

    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@ModelAttribute Usuario usuario, 
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        try {
            String email = authentication.getName();
            Usuario usuarioExistente = usuarioServicio.findByEmail(email);

            // Actualizar solo los campos permitidos
            usuarioExistente.setNombre(usuario.getNombre());
            usuarioExistente.setApellido(usuario.getApellido());
            usuarioExistente.setTelefono(usuario.getTelefono());
            usuarioExistente.setFechaNacimiento(usuario.getFechaNacimiento());

            usuarioServicio.actualizarUsuario(usuarioExistente);

            redirectAttributes.addFlashAttribute("exito", "Perfil actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
        }

        return "redirect:/perfil";
    }

    // MÉTODO PARA ACTUALIZAR EMAIL (si lo necesitas)
    @PostMapping("/perfil/actualizar-email")
    public String actualizarEmail(@RequestParam String nuevoEmail,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            String emailActual = authentication.getName();
            Usuario usuario = usuarioServicio.findByEmail(emailActual);
            
            // Actualizar email en credenciales usando el repositorio existente
            Optional<Credenciales> credencialesOpt = credencialesRepository.findByIdUsuario(usuario.getIdUsuario());
            if (credencialesOpt.isPresent()) {
                Credenciales credenciales = credencialesOpt.get();
                credenciales.setCorreo(nuevoEmail);
                credencialesRepository.save(credenciales);
            }
            
            redirectAttributes.addFlashAttribute("exito", "Email actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el email: " + e.getMessage());
        }

        return "redirect:/perfil";
    }

    @PostMapping("/perfil/direccion/guardar")
    public String guardarDireccion(
            @RequestParam(value = "idDireccion", required = false) Integer idDireccion,
            @RequestParam("nombreDireccion") String nombreDireccion,
            @RequestParam("direccion") String direccion,
            @RequestParam("numero") String numero,
            @RequestParam(value = "referencia", required = false) String referencia,
            @RequestParam("distrito") String distrito,
            @RequestParam("telefono") String telefono,
            @RequestParam(value = "principal", defaultValue = "false") boolean principal,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        try {
            String email = authentication.getName();
            Usuario usuario = usuarioServicio.findByEmail(email);

            Direccion direccionObj;
            
            if (idDireccion != null && idDireccion > 0) {
                // Editar dirección existente
                direccionObj = direccionServicio.obtenerDireccionPorIdYUsuario(idDireccion, usuario);
            } else {
                // Nueva dirección
                direccionObj = new Direccion();
                direccionObj.setUsuario(usuario);
            }

            // Actualizar campos
            direccionObj.setNombreDireccion(nombreDireccion);
            direccionObj.setDireccion(direccion);
            direccionObj.setNumero(numero);
            direccionObj.setReferencia(referencia);
            direccionObj.setDistrito(distrito);
            direccionObj.setTelefono(telefono);
            direccionObj.setPrincipal(principal);

            // Si se marca como principal, quitar principal de otras direcciones
            if (principal) {
                direccionServicio.quitarPrincipalDeOtrasDirecciones(usuario);
            }

            direccionServicio.guardar(direccionObj);

            redirectAttributes.addFlashAttribute("exito", "Dirección guardada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la dirección: " + e.getMessage());
        }

        return "redirect:/perfil";
    }

    @GetMapping("/perfil/direccion/obtener/{id}")
    @ResponseBody
    public Direccion obtenerDireccion(@PathVariable("id") int idDireccion, Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioServicio.findByEmail(email);
        
        return direccionServicio.obtenerDireccionPorIdYUsuario(idDireccion, usuario);
    }

    @PostMapping("/perfil/direccion/eliminar/{id}")
    public String eliminarDireccion(@PathVariable("id") int idDireccion,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        try {
            String email = authentication.getName();
            Usuario usuario = usuarioServicio.findByEmail(email);
            
            direccionServicio.eliminarDireccionPorIdYUsuario(idDireccion, usuario);
            
            redirectAttributes.addFlashAttribute("exito", "Dirección eliminada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la dirección: " + e.getMessage());
        }

        return "redirect:/perfil";
    }

    @PostMapping("/perfil/direccion/principal/{id}")
    public String establecerDireccionPrincipal(@PathVariable("id") int idDireccion,
                                              Authentication authentication,
                                              RedirectAttributes redirectAttributes) {
        try {
            String email = authentication.getName();
            Usuario usuario = usuarioServicio.findByEmail(email);

            direccionServicio.establecerDireccionPrincipal(usuario, idDireccion);

            redirectAttributes.addFlashAttribute("exito", "Dirección principal establecida correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al establecer dirección principal: " + e.getMessage());
        }

        return "redirect:/perfil";
    }
}