package com.ProyectoPaginaWeb.ProyectoPagina.modelo;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private Usuario usuario;
    private Credenciales credenciales;

    public CustomUserDetails(Usuario usuario, Credenciales credenciales) {
        this.usuario = usuario;
        this.credenciales = credenciales;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombreRol()));
    }

    @Override
    public String getPassword() {
        return credenciales.getContrasenia();
    }

    @Override
    public String getUsername() {
        return credenciales.getCorreo();
    }

    // Métodos para acceder a los datos del usuario
    public String getNombre() {
        return usuario.getNombre();
    }

    public String getApellido() {
        return usuario.getApellido();
    }

    public String getNombreCompleto() {
        return usuario.getNombre() + " " + usuario.getApellido();
    }

    public String getDni() {
        return usuario.getDni();
    }

    public String getTelefono() {
        return usuario.getTelefono();
    }

    public String getRol() {
        return usuario.getRol().getNombreRol();
    }

    public Usuario getUsuario() {
        return usuario;
    }

    // Métodos de UserDetails
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return usuario.getEstadoUsuario().getNombreEstado().equals("Activo");
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return usuario.getEstadoUsuario().getNombreEstado().equals("Activo");
    }
}
