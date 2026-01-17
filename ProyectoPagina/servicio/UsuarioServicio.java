// UsuarioServicio.java - ACTUALIZADO
package com.ProyectoPaginaWeb.ProyectoPagina.servicio;

import com.ProyectoPaginaWeb.ProyectoPagina.modelo.*;
import com.ProyectoPaginaWeb.ProyectoPagina.repositorio.UsuarioRepository;
import com.ProyectoPaginaWeb.ProyectoPagina.repositorio.CredencialesRepository;
import com.google.common.base.Preconditions;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioServicio {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CredencialesRepository credencialesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    // ============================================================
    // REGISTRO DE USUARIOS
    // ============================================================
    @Transactional
    public boolean registrarUsuario(Usuario usuario, String email, String password) {
        Preconditions.checkNotNull(usuario, "El usuario no puede ser nulo.");
        Preconditions.checkNotNull(email, "El email no puede ser nulo.");
        Preconditions.checkNotNull(password, "La contraseña no puede ser nula.");

        // Validar formato de email
        if (!esEmailValido(email)) {
            throw new IllegalArgumentException("El email no tiene un formato válido.");
        }

        // Verificar si el email ya existe en Credenciales
        if (credencialesRepository.existsByCorreo(email)) {
            return false;
        }

        // Verificar si el DNI ya existe
        if (usuarioRepository.existsByDni(usuario.getDni())) {
            return false;
        }

        // Validar complejidad de la contraseña
        if (!esPasswordValida(password)) {
            throw new IllegalArgumentException("La contraseña no cumple los requisitos de seguridad.");
        }

        try {
            // Encriptar la contraseña
            String passwordEncriptada = passwordEncoder.encode(password);
            
            // Guardar el usuario primero (sin credenciales)
            Usuario usuarioGuardado = usuarioRepository.save(usuario);

            // Crear y guardar las credenciales por separado
            Credenciales credenciales = new Credenciales();
            credenciales.setUsuario(usuarioGuardado);
            credenciales.setCorreo(email);
            credenciales.setContrasenia(passwordEncriptada);

            credencialesRepository.save(credenciales);

            return true;

        } catch (Exception e) {
            throw new RuntimeException("Error al registrar el usuario: " + e.getMessage(), e);
        }
    }

    // ============================================================
    // MÉTODOS PARA EL PERFIL
    // ============================================================

    /**
     * Busca un usuario por su email
     */
    public Usuario findByEmail(String email) {
        // Buscar usuario a través de credenciales
        Credenciales credenciales = credencialesRepository.findByCorreo(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
        return credenciales.getUsuario();
    }

    /**
     * Buscar usuario por email (alias)
     */
    public Optional<Usuario> buscarPorEmail(String email) {
        Optional<Credenciales> credenciales = credencialesRepository.findByCorreo(email);
        return credenciales.map(Credenciales::getUsuario);
    }

    /**
     * Verificar si email existe
     */
    public boolean existeEmail(String email) {
        return credencialesRepository.existsByCorreo(email);
    }

    /**
     * Actualiza la información del usuario en la base de datos
     */
    @Transactional
    public void actualizarUsuario(Usuario usuario) {
        Preconditions.checkNotNull(usuario, "El usuario no puede ser nulo.");
        Preconditions.checkNotNull(usuario.getIdUsuario(), "El ID del usuario no puede ser nulo.");

        // Verificar que el usuario exista
        Usuario usuarioExistente = usuarioRepository.findById(usuario.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuario.getIdUsuario()));

        // Actualizar los campos permitidos (no actualizamos email, DNI, ni password aquí)
        usuarioExistente.setNombre(usuario.getNombre());
        usuarioExistente.setApellido(usuario.getApellido());
        usuarioExistente.setTelefono(usuario.getTelefono());
        usuarioExistente.setFechaNacimiento(usuario.getFechaNacimiento());

        usuarioRepository.save(usuarioExistente);
    }

    // ============================================================
    // RECUPERAR CONTRASEÑA
    // ============================================================
    @Transactional
    public void enviarCodigoRecuperacion(String correo) {
        Preconditions.checkNotNull(correo, "El correo no puede ser nulo.");

        // Buscar en CredencialesRepository
        Optional<Credenciales> optionalCredenciales = credencialesRepository.findByCorreo(correo);
        if (optionalCredenciales.isEmpty()) {
            throw new RuntimeException("No se encontró una cuenta con ese correo.");
        }

        Credenciales credenciales = optionalCredenciales.get();
        Usuario usuario = credenciales.getUsuario();

        // Generar código seguro
        String raw = correo + UUID.randomUUID() + System.currentTimeMillis();
        String codigo = Hashing.sha256()
                .hashString(raw, StandardCharsets.UTF_8)
                .toString()
                .substring(0, 8)
                .toUpperCase();

        credenciales.setCodigoTemporal(codigo);
        credenciales.setFechaExpiraCodigo(LocalDateTime.now().plusMinutes(10));
        credencialesRepository.save(credenciales);

        // Enviar correo
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(correo);
        mensaje.setSubject("Código de Recuperación - Creaciones ARS");
        mensaje.setText("Hola " + usuario.getNombre() + ",\n\n" +
                "Tu código de recuperación es: " + codigo + "\n\n" +
                "Este código expirará en 10 minutos.\n\n" +
                "— Equipo de Creaciones ARS");

        mailSender.send(mensaje);
    }

    public boolean verificarCodigo(String correo, String codigoIngresado) {
        Preconditions.checkNotNull(correo, "El correo no puede ser nulo.");
        Preconditions.checkNotNull(codigoIngresado, "El código no puede ser nulo.");

        // Buscar en CredencialesRepository
        Optional<Credenciales> optionalCredenciales = credencialesRepository.findByCorreo(correo);
        if (optionalCredenciales.isEmpty()) return false;

        Credenciales credenciales = optionalCredenciales.get();
        if (credenciales.getCodigoTemporal() == null || credenciales.getFechaExpiraCodigo() == null) {
            return false;
        }

        return credenciales.getCodigoTemporal().equals(codigoIngresado)
                && credenciales.getFechaExpiraCodigo().isAfter(LocalDateTime.now());
    }

    @Transactional
    public void cambiarContrasena(String correo, String nuevaContrasena) {
        Preconditions.checkNotNull(correo, "El correo no puede ser nulo.");
        Preconditions.checkNotNull(nuevaContrasena, "La nueva contraseña no puede ser nula.");

        // Buscar en CredencialesRepository
        Optional<Credenciales> optionalCredenciales = credencialesRepository.findByCorreo(correo);
        if (optionalCredenciales.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        Credenciales credenciales = optionalCredenciales.get();

        // Validar nueva contraseña
        if (!esPasswordValida(nuevaContrasena)) {
            throw new IllegalArgumentException("La contraseña no cumple los requisitos de seguridad.");
        }

        String nuevaContrasenaEncriptada = passwordEncoder.encode(nuevaContrasena);
        
        // Actualizar solo en Credenciales
        credenciales.setContrasenia(nuevaContrasenaEncriptada);
        credenciales.setCodigoTemporal(null);
        credenciales.setFechaExpiraCodigo(null);
        credencialesRepository.save(credenciales);
    }

    // ============================================================
// MÉTODO PARA CREAR CREDENCIALES (PARA ADMIN)
// ============================================================

/**
 * Crea credenciales para un usuario ya existente (uso del administrador)
 */
@Transactional
public boolean crearCredenciales(Usuario usuario, String email, String password) {
    Preconditions.checkNotNull(usuario, "El usuario no puede ser nulo.");
    Preconditions.checkNotNull(email, "El email no puede ser nulo.");
    Preconditions.checkNotNull(password, "La contraseña no puede ser nula.");

    // Validar formato de email
    if (!esEmailValido(email)) {
        throw new IllegalArgumentException("El email no tiene un formato válido.");
    }

    // Verificar si el email ya existe en Credenciales
    if (credencialesRepository.existsByCorreo(email)) {
        return false;
    }

    // Validar longitud mínima de contraseña (menos estricto para admin)
    if (password.length() < 6) {
        throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
    }

    try {
        // Encriptar la contraseña
        String passwordEncriptada = passwordEncoder.encode(password);
        
        // Crear y guardar las credenciales
        Credenciales credenciales = new Credenciales();
        credenciales.setUsuario(usuario);
        credenciales.setCorreo(email);
        credenciales.setContrasenia(passwordEncriptada);

        credencialesRepository.save(credenciales);

        return true;

    } catch (Exception e) {
        throw new RuntimeException("Error al crear las credenciales: " + e.getMessage(), e);
    }
}

    // ============================================================
    // MÉTODOS ACTUALIZACIÓN
    // ============================================================

    /**
     * Actualiza solo la contraseña del usuario
     */
    @Transactional
    public void actualizarPassword(String email, String nuevaPassword) {
        Preconditions.checkNotNull(email, "El email no puede ser nulo.");
        Preconditions.checkNotNull(nuevaPassword, "La nueva contraseña no puede ser nula.");

        if (!esPasswordValida(nuevaPassword)) {
            throw new IllegalArgumentException("La contraseña no cumple los requisitos de seguridad.");
        }

        String nuevaPasswordEncriptada = passwordEncoder.encode(nuevaPassword);

        // Actualizar solo en Credenciales
        Optional<Credenciales> credencialesOpt = credencialesRepository.findByCorreo(email);
        if (credencialesOpt.isPresent()) {
            Credenciales credenciales = credencialesOpt.get();
            credenciales.setContrasenia(nuevaPasswordEncriptada);
            credencialesRepository.save(credenciales);
        }
    }

    // ============================================================
    // MÉTODOS AUXILIARES
    // ============================================================
    
    /**
     * Buscar credenciales por ID de usuario
     */
    public Optional<Credenciales> buscarCredencialesPorIdUsuario(int idUsuario) {
        return credencialesRepository.findByIdUsuario(idUsuario);
    }

    /**
     * Obtener usuario por ID
     */
    public Usuario findById(int idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));
    }

    // ============================================================
    // VALIDACIONES INTERNAS
    // ============================================================
    private boolean esPasswordValida(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.#_])[A-Za-z\\d@$!%*?&.#_]{8,}$";
        return password != null && password.matches(regex);
    }

    private boolean esEmailValido(String email) {
        String regex = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email != null && email.matches(regex);
    }
}