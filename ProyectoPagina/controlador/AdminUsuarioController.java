package com.ProyectoPaginaWeb.ProyectoPagina.controlador;

import com.ProyectoPaginaWeb.ProyectoPagina.modelo.*;
import com.ProyectoPaginaWeb.ProyectoPagina.repositorio.UsuarioRepository;
import com.ProyectoPaginaWeb.ProyectoPagina.repositorio.EstadoUsuarioRepository;
import com.ProyectoPaginaWeb.ProyectoPagina.repositorio.RolRepository;
import com.ProyectoPaginaWeb.ProyectoPagina.servicio.UsuarioServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final EstadoUsuarioRepository estadoUsuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioServicio usuarioServicio;

    // ✅ ELIMINADO: credencialesRepository ya no se usa directamente
    public AdminUsuarioController(UsuarioRepository usuarioRepository,
                                EstadoUsuarioRepository estadoUsuarioRepository,
                                RolRepository rolRepository,
                                UsuarioServicio usuarioServicio) {
        this.usuarioRepository = usuarioRepository;
        this.estadoUsuarioRepository = estadoUsuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioServicio = usuarioServicio;
    }

    @GetMapping
    public String listarUsuarios(Model model) {
        try {
            // Cargar usuarios con estados, roles y permisos
            List<Usuario> usuarios = usuarioRepository.findAllWithRolAndEstadoAndPermisos();
            
            // Calcular estadísticas usando los métodos corregidos
            long totalUsuarios = usuarioRepository.count();
            long usuariosActivos = usuarioRepository.countByEstadoId(1); // Asumiendo que 1 es Activo
            long usuariosInactivos = usuarioRepository.countByEstadoId(2); // Asumiendo que 2 es Inactivo
            long administradores = usuarioRepository.countByRolId(1); // Asumiendo que 1 es Administrador
            
            model.addAttribute("usuarios", usuarios);
            model.addAttribute("totalUsuarios", totalUsuarios);
            model.addAttribute("usuariosActivos", usuariosActivos);
            model.addAttribute("usuariosInactivos", usuariosInactivos);
            model.addAttribute("administradores", administradores);
            model.addAttribute("estados", estadoUsuarioRepository.findAll());
            model.addAttribute("roles", rolRepository.findAll());
            model.addAttribute("usuario", new Usuario());
            
        } catch (Exception e) {
            // En caso de error, establecer valores por defecto
            model.addAttribute("error", "Error al cargar usuarios: " + e.getMessage());
            model.addAttribute("usuarios", List.of());
            model.addAttribute("totalUsuarios", 0);
            model.addAttribute("usuariosActivos", 0);
            model.addAttribute("usuariosInactivos", 0);
            model.addAttribute("administradores", 0);
        }
        
        return "admin/lista-usuarios";
    }

    @PostMapping("/registrar")
    public String registrarUsuario(@ModelAttribute Usuario usuario,
                                 @RequestParam String correo,
                                 @RequestParam String password,
                                 @RequestParam String confirmPassword,
                                 @RequestParam Integer rolId,
                                 @RequestParam Integer estadoId,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Validaciones de campos únicos
            if (usuarioRepository.existsByDni(usuario.getDni())) {
                redirectAttributes.addFlashAttribute("error", "El DNI ya está registrado");
                return "redirect:/admin/usuarios";
            }
            
            if (usuarioRepository.existsByTelefono(usuario.getTelefono())) {
                redirectAttributes.addFlashAttribute("error", "El teléfono ya está registrado");
                return "redirect:/admin/usuarios";
            }

            // Validar que el correo no esté en uso
            if (usuarioRepository.existsByCorreo(correo)) {
                redirectAttributes.addFlashAttribute("error", "El correo electrónico ya está registrado");
                return "redirect:/admin/usuarios";
            }

            // Validar que las contraseñas coincidan
            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
                return "redirect:/admin/usuarios";
            }

            // Validar longitud mínima de contraseña
            if (password.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres");
                return "redirect:/admin/usuarios";
            }

            // Validar fecha de nacimiento (mayor de 18 años)
            LocalDate fechaMinima = LocalDate.now().minusYears(18);
            if (usuario.getFechaNacimiento().isAfter(fechaMinima)) {
                redirectAttributes.addFlashAttribute("error", "El usuario debe ser mayor de 18 años");
                return "redirect:/admin/usuarios";
            }
            
            // Configurar relaciones
            Rol rol = rolRepository.findById(rolId)
                    .orElseThrow(() -> new IllegalArgumentException("Rol no válido"));
            usuario.setRol(rol);
            
            EstadoUsuario estado = estadoUsuarioRepository.findById(estadoId)
                    .orElseThrow(() -> new IllegalArgumentException("Estado no válido"));
            usuario.setEstadoUsuario(estado);
            
            // Guardar usuario
            usuarioRepository.save(usuario);
            
            // ✅ CORREGIDO: Usar el nuevo método crearCredenciales
            boolean credencialesCreadas = usuarioServicio.crearCredenciales(usuario, correo, password);
            
            if (credencialesCreadas) {
                redirectAttributes.addFlashAttribute("success", "Usuario registrado exitosamente");
            } else {
                // Si falla la creación de credenciales, eliminar el usuario creado
                usuarioRepository.delete(usuario);
                redirectAttributes.addFlashAttribute("error", "Error al crear las credenciales del usuario");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar usuario: " + e.getMessage());
        }
        
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/cambiar-estado/{id}")
    public String cambiarEstadoUsuario(@PathVariable Integer id,
                                     @RequestParam Integer nuevoEstadoId,
                                     RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            
            EstadoUsuario nuevoEstado = estadoUsuarioRepository.findById(nuevoEstadoId)
                    .orElseThrow(() -> new IllegalArgumentException("Estado no válido"));
            
            usuario.setEstadoUsuario(nuevoEstado);
            usuarioRepository.save(usuario);
            
            redirectAttributes.addFlashAttribute("success", "Estado del usuario actualizado exitosamente");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar estado: " + e.getMessage());
        }
        
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/cambiar-rol/{id}")
    public String cambiarRolUsuario(@PathVariable Integer id,
                                  @RequestParam Integer nuevoRolId,
                                  RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            
            Rol nuevoRol = rolRepository.findById(nuevoRolId)
                    .orElseThrow(() -> new IllegalArgumentException("Rol no válido"));
            
            usuario.setRol(nuevoRol);
            usuarioRepository.save(usuario);
            
            redirectAttributes.addFlashAttribute("success", "Rol del usuario actualizado exitosamente");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar rol: " + e.getMessage());
        }
        
        return "redirect:/admin/usuarios";
    }
}