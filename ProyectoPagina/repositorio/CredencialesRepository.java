package com.ProyectoPaginaWeb.ProyectoPagina.repositorio;

import com.ProyectoPaginaWeb.ProyectoPagina.modelo.Credenciales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CredencialesRepository extends JpaRepository<Credenciales, Integer> {
    
    // Buscar por correo (que es único)
    Optional<Credenciales> findByCorreo(String correo);
    
    // Verificar si existe por correo
    boolean existsByCorreo(String correo);
    
    // Buscar por código temporal
    Optional<Credenciales> findByCodigoTemporal(String codigoTemporal);
    
    // CORREGIDO: Buscar credenciales por ID de usuario usando el nombre correcto del campo
    @Query("SELECT c FROM Credenciales c WHERE c.usuario.idUsuario = :idUsuario")
    Optional<Credenciales> findByIdUsuario(@Param("idUsuario") int idUsuario);

}