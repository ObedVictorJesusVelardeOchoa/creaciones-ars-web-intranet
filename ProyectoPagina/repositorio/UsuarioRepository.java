package com.ProyectoPaginaWeb.ProyectoPagina.repositorio;

import com.ProyectoPaginaWeb.ProyectoPagina.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    // Buscar por DNI
    Optional<Usuario> findByDni(String dni);
    
    // Verificar si existe por DNI
    boolean existsByDni(String dni);
    
    // Verificar si existe por teléfono
    boolean existsByTelefono(String telefono);
    
    // Cargar usuarios con todas las relaciones
    @Query("SELECT DISTINCT u FROM Usuario u " +
           "LEFT JOIN FETCH u.rol r " +
           "LEFT JOIN FETCH r.permisos " +
           "LEFT JOIN FETCH u.estadoUsuario " +
           "ORDER BY u.fechaRegistro DESC")
    List<Usuario> findAllWithRolAndEstadoAndPermisos();
    
    // Contar usuarios por estado
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.estadoUsuario.id = :estadoId")
    Long countByEstadoId(@Param("estadoId") Integer estadoId);
    
    // Contar usuarios por rol
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol.idRol = :rolId")
    Long countByRolId(@Param("rolId") Integer rolId);
    
    // Buscar usuarios por estado
    @Query("SELECT u FROM Usuario u WHERE u.estadoUsuario.id = :estadoId")
    List<Usuario> findByEstadoId(@Param("estadoId") Integer estadoId);
    
    // Buscar usuarios por rol
    @Query("SELECT u FROM Usuario u WHERE u.rol.idRol = :rolId")
    List<Usuario> findByRolId(@Param("rolId") Integer rolId);
    
    // Métodos derivados para buscar por estado y rol
    List<Usuario> findByEstadoUsuario_Id(Integer id);
    List<Usuario> findByRol_IdRol(Integer idRol);
    
    // ✅ CORREGIDO: Solo mantener este método para verificar correo
    @Query("SELECT COUNT(c) > 0 FROM Credenciales c WHERE c.correo = :correo")
    boolean existsByCorreo(@Param("correo") String correo);
    
}