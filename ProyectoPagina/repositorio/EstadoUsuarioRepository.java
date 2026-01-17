package com.ProyectoPaginaWeb.ProyectoPagina.repositorio;

import com.ProyectoPaginaWeb.ProyectoPagina.modelo.EstadoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EstadoUsuarioRepository extends JpaRepository<EstadoUsuario, Integer> {
    Optional<EstadoUsuario> findByNombreEstado(String nombreEstado);
}