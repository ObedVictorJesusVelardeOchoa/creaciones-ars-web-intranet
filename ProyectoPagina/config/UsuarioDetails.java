package com.ProyectoPaginaWeb.ProyectoPagina.config;

import com.ProyectoPaginaWeb.ProyectoPagina.modelo.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UsuarioDetails implements UserDetails {
    
    private final Usuario usuario;
    private final String password;
    private final String username;
    
    public UsuarioDetails(Usuario usuario, String password, String username) {
        this.usuario = usuario;
        this.password = password;
        this.username = username;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // ✅ CORREGIDO: Asegúrate de que Rol tenga getNombreRol() o getNombre()
        String nombreRol = usuario.getRol().getNombreRol(); // o getNombre() dependiendo de tu entidad Rol
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + nombreRol));
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    // ✅ Métodos personalizados para obtener información del usuario
    public String getNombreCompleto() {
        return usuario.getNombreCompleto();
    }
    
    public String getNombre() {
        return usuario.getNombre();
    }
    
    public String getApellido() {
        return usuario.getApellido();
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    // Implementaciones restantes de UserDetails
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
}