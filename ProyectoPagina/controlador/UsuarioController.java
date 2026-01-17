// UsuarioController.java - CORREGIDO
package com.ProyectoPaginaWeb.ProyectoPagina.controlador;

import com.ProyectoPaginaWeb.ProyectoPagina.modelo.*;
import com.ProyectoPaginaWeb.ProyectoPagina.servicio.UsuarioServicio;
import com.ProyectoPaginaWeb.ProyectoPagina.repositorio.RolRepository;
import com.ProyectoPaginaWeb.ProyectoPagina.repositorio.EstadoUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private RolRepository rolRepositorio;

    @Autowired
    private EstadoUsuarioRepository estadoUsuarioRepositorio;

    // ✅ ELIMINADO: CredencialesRepository no es necesario aquí
    // Todas las operaciones se manejan a través de UsuarioServicio

    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(
            @RequestParam("nombre") String nombre,
            @RequestParam("apellido") String apellido,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("dni") String dni,
            @RequestParam("telefono") String telefono,
            @RequestParam("fechaNacimiento") String fechaNacimientoStr,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Validaciones básicas
            if (nombre == null || nombre.trim().isEmpty() ||
                apellido == null || apellido.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                dni == null || dni.trim().isEmpty() ||
                telefono == null || telefono.trim().isEmpty()) {
                
                redirectAttributes.addFlashAttribute("error", "Todos los campos son obligatorios.");
                return "redirect:/registro";
            }

            // Validar que las contraseñas coincidan
            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden.");
                return "redirect:/registro";
            }

            // Convertir fecha de nacimiento
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate fechaNacimiento = LocalDate.parse(fechaNacimientoStr, formatter);

            // Validar edad (mayor de 18 años)
            if (fechaNacimiento.isAfter(LocalDate.now().minusYears(18))) {
                redirectAttributes.addFlashAttribute("error", "Debes ser mayor de 18 años para registrarte.");
                return "redirect:/registro";
            }

            // Obtener rol por defecto (Usuario)
            Optional<Rol> rolUsuario = rolRepositorio.findByNombreRol("Usuario");
            if (rolUsuario.isEmpty()) {
                Rol nuevoRol = new Rol("Usuario");
                rolRepositorio.save(nuevoRol);
                rolUsuario = Optional.of(nuevoRol);
            }

            // Obtener estado por defecto (Activo)
            Optional<EstadoUsuario> estadoActivo = estadoUsuarioRepositorio.findByNombreEstado("Activo");
            if (estadoActivo.isEmpty()) {
                EstadoUsuario nuevoEstado = new EstadoUsuario("Activo");
                estadoUsuarioRepositorio.save(nuevoEstado);
                estadoActivo = Optional.of(nuevoEstado);
            }

            // ✅ Crear usuario sin email y password
            Usuario usuario = new Usuario(
                nombre.trim(),
                apellido.trim(),
                dni.trim(),
                telefono.trim(),
                fechaNacimiento,
                rolUsuario.get(),
                estadoActivo.get()
            );

            // ✅ Registrar usando el servicio actualizado
            boolean registrado = usuarioServicio.registrarUsuario(usuario, email.trim(), password);

            if (registrado) {
                redirectAttributes.addFlashAttribute("registroExitoso", true);
                return "redirect:/registro";
            } else {
                redirectAttributes.addFlashAttribute("error", "El correo o DNI ya está registrado.");
                return "redirect:/registro";
            }

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", 
                "Error en el registro: " + e.getMessage());
            return "redirect:/registro";
        }
    }
}