package com.ProyectoPaginaWeb.ProyectoPagina.repositorio;

import com.ProyectoPaginaWeb.ProyectoPagina.modelo.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    
    // Consultas básicas
    boolean existsByNumeroSerie(String numeroSerie);
    
    Optional<Producto> findByNumeroSerie(String numeroSerie);
    
    List<Producto> findByCategoriaId(Integer categoriaId);
    
    List<Producto> findByGenero(String genero);
    
    // ========== CONSULTAS OPTIMIZADAS CORREGIDAS ==========
    
    @Query("SELECT DISTINCT p FROM Producto p " +
           "LEFT JOIN FETCH p.categoria c " +
           "ORDER BY p.id")
    List<Producto> findAllWithCategoria();
    
    @Query("SELECT p FROM Producto p LEFT JOIN p.categoria c ORDER BY p.id")
    List<Producto> findAllWithCategoriaSimple();
    
    // ========== CONSULTAS PARA BÚSQUEDA ==========
    
    @Query("SELECT p FROM Producto p WHERE " +
           "LOWER(p.modelo) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(p.numeroSerie) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(p.genero) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(p.categoria.name) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<Producto> buscarPorTermino(@Param("termino") String termino);
    
    // ========== CONSULTAS PARA ESTADÍSTICAS ==========
    
    @Query("SELECT COUNT(DISTINCT p) FROM Producto p JOIN p.tallas t WHERE t.stock > 0")
    Long countProductosActivos();
    
    @Query("SELECT AVG(p.precio) FROM Producto p")
    Double findPrecioPromedio();
    
    @Query("SELECT p FROM Producto p ORDER BY p.precio DESC LIMIT 1")
    Optional<Producto> findProductoMasCaro();
    
    @Query("SELECT p FROM Producto p WHERE p.precio > 0 ORDER BY p.precio ASC LIMIT 1")
    Optional<Producto> findProductoMasBarato();
    
    @Query("SELECT p FROM Producto p ORDER BY p.id DESC LIMIT :limite")
    List<Producto> findProductosRecientes(@Param("limite") int limite);
    
    // ========== NUEVAS CONSULTAS PARA STOCK ==========
    
    // Consulta para productos con stock bajo
    @Query("SELECT p FROM Producto p WHERE p.stockTotal <= :limiteStock")
    List<Producto> findProductosConStockBajo(@Param("limiteStock") Integer limiteStock);
    
    // Sumar stock total de todos los productos
    @Query("SELECT COALESCE(SUM(p.stockTotal), 0) FROM Producto p")
    Long sumStockTotal();
    
    // Contar productos sin stock
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.stockTotal = 0")
    Long countProductosSinStock();
}