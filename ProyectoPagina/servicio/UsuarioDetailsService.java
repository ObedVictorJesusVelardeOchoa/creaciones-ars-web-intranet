package com.ProyectoPaginaWeb.ProyectoPagina.servicio;

import com.ProyectoPaginaWeb.ProyectoPagina.config.UsuarioDetails;
import com.ProyectoPaginaWeb.ProyectoPagina.modelo.Credenciales;
import com.ProyectoPaginaWeb.ProyectoPagina.modelo.Usuario;
import com.ProyectoPaginaWeb.ProyectoPagina.repositorio.CredencialesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    @Autowired
    private CredencialesRepository credencialesRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        // Buscar las credenciales por correo
        Credenciales credenciales = credencialesRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        // Obtener el usuario asociado a las credenciales
        Usuario usuario = credenciales.getUsuario();

        // ✅ CORREGIDO: Retornar UsuarioDetails con la información correcta
        return new UsuarioDetails(
            usuario, 
            credenciales.getContrasenia(), // asumiendo que se llama getContrasena()
            credenciales.getCorreo()      // username es el correo
        );
    }
}