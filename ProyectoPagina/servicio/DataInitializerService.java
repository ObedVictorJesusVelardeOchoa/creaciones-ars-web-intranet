package com.ProyectoPaginaWeb.ProyectoPagina.servicio;

import com.ProyectoPaginaWeb.ProyectoPagina.modelo.*;
import com.ProyectoPaginaWeb.ProyectoPagina.repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

@Service
@Transactional
public class DataInitializerService implements CommandLineRunner {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private EstadoUsuarioRepository estadoUsuarioRepository;

    @Autowired
    private PermisoRepository permisoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CredencialesRepository credencialesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeRolesAndPermissions();
        initializeEstadosUsuario();
        createAdminUser(); // ✅ Crear usuario administrador por defecto
    }

    private void initializeEstadosUsuario() {
        // Verificar y crear estados de usuario si no existen
        if (estadoUsuarioRepository.count() == 0) {
            EstadoUsuario activo = new EstadoUsuario("Activo");
            EstadoUsuario inactivo = new EstadoUsuario("Inactivo");
            EstadoUsuario bloqueado = new EstadoUsuario("Bloqueado");
            
            estadoUsuarioRepository.saveAll(Arrays.asList(activo, inactivo, bloqueado));
            System.out.println("✅ Estados de usuario inicializados correctamente");
        }
    }

    private void initializeRolesAndPermissions() {
        // Verificar y crear permisos si no existen
        if (permisoRepository.count() == 0) {
            Permiso gestionarUsuarios = new Permiso("GESTIONAR_USUARIOS");
            Permiso gestionarProductos = new Permiso("GESTIONAR_PRODUCTOS");
            Permiso gestionarPedidos = new Permiso("GESTIONAR_PEDIDOS");
            Permiso verReportes = new Permiso("VER_REPORTES");
            
            permisoRepository.saveAll(Arrays.asList(gestionarUsuarios, gestionarProductos, gestionarPedidos, verReportes));
            System.out.println("✅ Permisos inicializados correctamente");
        }

        // Verificar y crear roles si no existen
        if (rolRepository.count() == 0) {
            Rol administrador = new Rol("Administrador");
            Rol usuario = new Rol("Usuario");
            
            // Asignar permisos a administrador
            administrador.setPermisos(new HashSet<>(permisoRepository.findAll()));
            
            // Asignar permisos a usuario (solo los básicos)
            usuario.setPermisos(new HashSet<>(Arrays.asList(
                permisoRepository.findByNombrePermiso("GESTIONAR_PEDIDOS").orElseThrow(),
                permisoRepository.findByNombrePermiso("VER_REPORTES").orElseThrow()
            )));
            
            rolRepository.saveAll(Arrays.asList(administrador, usuario));
            System.out.println("✅ Roles y permisos inicializados correctamente");
        }
    }

    private void createAdminUser() {
    try {
        // Verificar si ya existe un administrador por email en Credenciales
        Optional<Credenciales> adminCredencialExistente = credencialesRepository.findByCorreo("admin@creacionesars.com");
        if (adminCredencialExistente.isEmpty()) {
            // Obtener el rol de administrador
            Rol rolAdmin = rolRepository.findByNombreRol("Administrador")
                .orElseThrow(() -> new RuntimeException("❌ Rol Administrador no encontrado"));
            
            // Obtener el estado activo
            EstadoUsuario estadoActivo = estadoUsuarioRepository.findByNombreEstado("Activo")
                .orElseThrow(() -> new RuntimeException("❌ Estado Activo no encontrado"));
            
            // ✅ MODIFICADO: Crear usuario administrador sin email y password
            Usuario admin = new Usuario();
            admin.setNombre("Admin");
            admin.setApellido("Sistema");
            admin.setDni("00000000");
            admin.setTelefono("999999999");
            admin.setFechaNacimiento(LocalDate.of(1990, 1, 1));
            admin.setRol(rolAdmin);
            admin.setEstadoUsuario(estadoActivo);
            
            // Guardar el usuario
            Usuario adminGuardado = usuarioRepository.save(admin);
            
            // ✅ MODIFICADO: Crear credenciales para el administrador
            Credenciales credencialesAdmin = new Credenciales();
            credencialesAdmin.setUsuario(adminGuardado);
            credencialesAdmin.setCorreo("admin@creacionesars.com");
            credencialesAdmin.setContrasenia(passwordEncoder.encode("Admin123!"));
            
            credencialesRepository.save(credencialesAdmin);
            
            System.out.println("✅ Usuario administrador creado exitosamente");
            System.out.println("📧 Email: admin@creacionesars.com");
            System.out.println("🔑 Contraseña: Admin123!");
        } else {
            System.out.println("ℹ️  Usuario administrador ya existe: admin@creacionesars.com");
        }
    } catch (Exception e) {
        System.err.println("❌ Error al crear usuario administrador: " + e.getMessage());
        e.printStackTrace();
    }
  }
}