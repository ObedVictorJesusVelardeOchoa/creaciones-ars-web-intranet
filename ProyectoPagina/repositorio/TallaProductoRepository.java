package com.ProyectoPaginaWeb.ProyectoPagina.repositorio;

import com.ProyectoPaginaWeb.ProyectoPagina.modelo.TallaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TallaProductoRepository extends JpaRepository<TallaProducto, Integer> {
    
    // Consultas básicas
    List<TallaProducto> findByProductoId(Integer productoId);
    
    Optional<TallaProducto> findByProductoIdAndTalla(Integer productoId, String talla);
    
    boolean existsByProductoId(Integer productoId);
    
    boolean existsByProductoIdAndTalla(Integer productoId, String talla);
    
    // ========== CONSULTAS PARA ESTADÍSTICAS ==========
    
    /**
     * Suma total de stock de todos los productos
     */
    @Query("SELECT COALESCE(SUM(tp.stock), 0) FROM TallaProducto tp")
    Long sumTotalStock();
    
    /**
     * Contar productos que tienen al menos una talla con stock bajo (1-10 unidades)
     */
    @Query("SELECT COUNT(DISTINCT tp.producto.id) FROM TallaProducto tp WHERE tp.stock BETWEEN 1 AND 10")
    Long countProductosConStockBajo();
    
    /**
     * Contar productos que no tienen stock en ninguna talla
     */
    @Query("SELECT COUNT(DISTINCT p.id) FROM Producto p WHERE p.id NOT IN " +
           "(SELECT DISTINCT tp2.producto.id FROM TallaProducto tp2 WHERE tp2.stock > 0)")
    Long countProductosSinStock();
    
    /**
     * Obtener el stock total por producto
     */
    @Query("SELECT tp.producto.id, SUM(tp.stock) FROM TallaProducto tp GROUP BY tp.producto.id")
    List<Object[]> findStockTotalPorProducto();
    
    /**
     * Contar tallas con stock crítico (menos de 5 unidades)
     */
    @Query("SELECT COUNT(tp) FROM TallaProducto tp WHERE tp.stock > 0 AND tp.stock < 5")
    Long countTallasConStockCritico();
    
    /**
     * Contar tallas sin stock
     */
    @Query("SELECT COUNT(tp) FROM TallaProducto tp WHERE tp.stock = 0")
    Long countTallasSinStock();
    
    // ========== CONSULTAS PARA GESTIÓN DE STOCK ==========
    
    /**
     * Reducir stock de manera segura (solo si hay suficiente stock)
     */
    @Modifying
    @Query("UPDATE TallaProducto tp SET tp.stock = tp.stock - :cantidad " +
           "WHERE tp.producto.id = :productoId AND tp.talla = :talla AND tp.stock >= :cantidad")
    int reducirStock(@Param("productoId") Integer productoId, 
                     @Param("talla") String talla, 
                     @Param("cantidad") Integer cantidad);
    
    /**
     * Aumentar stock
     */
    @Modifying
    @Query("UPDATE TallaProducto tp SET tp.stock = tp.stock + :cantidad " +
           "WHERE tp.producto.id = :productoId AND tp.talla = :talla")
    int aumentarStock(@Param("productoId") Integer productoId, 
                      @Param("talla") String talla, 
                      @Param("cantidad") Integer cantidad);
    
    /**
     * Actualizar stock específico
     */
    @Modifying
    @Query("UPDATE TallaProducto tp SET tp.stock = :nuevoStock " +
           "WHERE tp.producto.id = :productoId AND tp.talla = :talla")
    int actualizarStock(@Param("productoId") Integer productoId, 
                        @Param("talla") String talla, 
                        @Param("nuevoStock") Integer nuevoStock);
    
    /**
     * Verificar disponibilidad de stock
     */
    @Query("SELECT CASE WHEN COUNT(tp) > 0 THEN true ELSE false END " +
           "FROM TallaProducto tp WHERE tp.producto.id = :productoId AND tp.talla = :talla AND tp.stock >= :cantidad")
    boolean verificarStockDisponible(@Param("productoId") Integer productoId, 
                                     @Param("talla") String talla, 
                                     @Param("cantidad") Integer cantidad);
    
    /**
     * Obtener stock disponible para una talla específica
     */
    @Query("SELECT COALESCE(tp.stock, 0) FROM TallaProducto tp " +
           "WHERE tp.producto.id = :productoId AND tp.talla = :talla")
    Integer obtenerStockPorProductoYTalla(@Param("productoId") Integer productoId, 
                                          @Param("talla") String talla);
    
    // ========== CONSULTAS PARA REPORTES ==========
    
    /**
     * Obtener productos con stock más bajo (para reabastecimiento)
     */
    @Query("SELECT tp FROM TallaProducto tp WHERE tp.stock > 0 ORDER BY tp.stock ASC")
    List<TallaProducto> findTallasConMenorStock();
    
    /**
     * Obtener productos con stock más alto
     */
    @Query("SELECT tp FROM TallaProducto tp WHERE tp.stock > 0 ORDER BY tp.stock DESC")
    List<TallaProducto> findTallasConMayorStock();
    
    /**
     * Obtener tallas que están por agotarse (stock < 10)
     */
    @Query("SELECT tp FROM TallaProducto tp WHERE tp.stock > 0 AND tp.stock < 10 ORDER BY tp.stock ASC")
    List<TallaProducto> findTallasPorAgotarse();
    
    /**
     * Obtener estadísticas de stock por categoría
     */
    @Query("SELECT p.categoria.name, SUM(tp.stock) FROM TallaProducto tp " +
           "JOIN tp.producto p GROUP BY p.categoria.name")
    List<Object[]> findStockPorCategoria();
    
    /**
     * Obtener estadísticas de stock por género
     */
    @Query("SELECT p.genero, SUM(tp.stock) FROM TallaProducto tp " +
           "JOIN tp.producto p GROUP BY p.genero")
    List<Object[]> findStockPorGenero();
    
    // ========== CONSULTAS DE LIMPIEZA ==========
    
    /**
     * Eliminar tallas de un producto específico
     */
    @Modifying
    @Query("DELETE FROM TallaProducto tp WHERE tp.producto.id = :productoId")
    void deleteByProductoId(@Param("productoId") Integer productoId);
    
    /**
     * Eliminar tallas sin stock
     */
    @Modifying
    @Query("DELETE FROM TallaProducto tp WHERE tp.stock = 0")
    void deleteTallasSinStock();
    
    /**
     * Eliminar tallas duplicadas (mismo producto y talla)
     */
    @Modifying
    @Query(value = "DELETE tp1 FROM talla_producto tp1 " +
           "INNER JOIN talla_producto tp2 ON tp1.producto_id = tp2.producto_id AND tp1.talla = tp2.talla " +
           "WHERE tp1.id > tp2.id", nativeQuery = true)
    void eliminarTallasDuplicadas();
}