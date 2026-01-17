package com.ProyectoPaginaWeb.ProyectoPagina.repositorio;

import com.ProyectoPaginaWeb.ProyectoPagina.modelo.Direccion;
import com.ProyectoPaginaWeb.ProyectoPagina.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Integer> {
    
    List<Direccion> findByUsuarioOrderByPrincipalDesc(Usuario usuario);
    
    Optional<Direccion> findByIdDireccionAndUsuario(int idDireccion, Usuario usuario);
    
    List<Direccion> findByUsuarioAndPrincipalTrue(Usuario usuario);
    
    List<Direccion> findByUsuario(Usuario usuario);
}