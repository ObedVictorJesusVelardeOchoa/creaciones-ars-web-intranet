package com.ProyectoPaginaWeb.ProyectoPagina.repositorio;

import com.ProyectoPaginaWeb.ProyectoPagina.modelo.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    
    // ==================== MÉTODOS DERIVADOS CORREGIDOS ====================
    
    // Historial de pedidos por cliente (usuario) - CORREGIDO
    List<Pedido> findByUsuario_IdUsuario(Integer idUsuario);
    
    // Filtrar por estado
    List<Pedido> findByEstado(String estado);
    
    // Filtrar por fecha
    List<Pedido> findByFechaPedidoBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    // ==================== MÉTODOS CON @Query ====================
    
    // Reporte de ventas diarias - MODIFICADO: Incluye estados relevantes
    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE DATE(p.fechaPedido) = CURRENT_DATE AND p.estado IN ('COMPLETADO', 'ENTREGADO', 'PAGADO')")
    Double findVentasDiarias();
    
    // Ventas de hoy sin filtrar por estado (para dashboard en tiempo real)
    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE DATE(p.fechaPedido) = CURRENT_DATE")
    Double findVentasHoy();
    
    // Pedidos del día actual para dashboard
    @Query("SELECT p FROM Pedido p WHERE DATE(p.fechaPedido) = CURRENT_DATE ORDER BY p.fechaPedido DESC")
    List<Pedido> findPedidosHoy();
    
    // Contar pedidos del día actual
    @Query("SELECT COUNT(p) FROM Pedido p WHERE DATE(p.fechaPedido) = CURRENT_DATE")
    Long countPedidosHoy();
    
    // Reporte de ventas semanales
    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE WEEK(p.fechaPedido) = WEEK(CURRENT_DATE) AND YEAR(p.fechaPedido) = YEAR(CURRENT_DATE) AND p.estado = 'ENTREGADO'")
    Double findVentasSemanales();
    
    // Reporte de ventas mensuales
    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE MONTH(p.fechaPedido) = MONTH(CURRENT_DATE) AND YEAR(p.fechaPedido) = YEAR(CURRENT_DATE) AND p.estado = 'ENTREGADO'")
    Double findVentasMensuales();
    
    // Contar pedidos por estado
    @Query("SELECT p.estado, COUNT(p) FROM Pedido p GROUP BY p.estado")
    List<Object[]> countPedidosByEstado();
    
    // Ventas por período personalizado
    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE p.fechaPedido BETWEEN :inicio AND :fin AND p.estado IN ('COMPLETADO', 'ENTREGADO', 'PAGADO')")
    Double findVentasByPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    // Top clientes (por compras) - CORREGIDO: usamos idUsuario
    @Query("SELECT p.usuario, SUM(p.total) as totalComprado FROM Pedido p WHERE p.estado IN ('COMPLETADO', 'ENTREGADO', 'PAGADO') GROUP BY p.usuario ORDER BY totalComprado DESC")
    List<Object[]> findTopClientes(@Param("limite") int limite);
    
    // Buscar pedidos con todos los detalles cargados - CORREGIDO
    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.detalles d LEFT JOIN FETCH d.producto WHERE p.id = :id")
    Pedido findByIdWithDetalles(@Param("id") Integer id);
    
    // Buscar pedidos de un usuario con detalles - CORREGIDO
    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.detalles d LEFT JOIN FETCH d.producto WHERE p.usuario.idUsuario = :usuarioId ORDER BY p.fechaPedido DESC")
    List<Pedido> findByUsuarioIdWithDetalles(@Param("usuarioId") Integer idUsuario);
    
    // ==================== MÉTODOS ADICIONALES ÚTILES ====================
    
    // Contar pedidos de un usuario
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.usuario.idUsuario = :idUsuario")
    Long countByUsuarioId(@Param("idUsuario") Integer idUsuario);
    
    // Encontrar pedido más reciente de un usuario
    @Query("SELECT p FROM Pedido p WHERE p.usuario.idUsuario = :idUsuario ORDER BY p.fechaPedido DESC LIMIT 1")
    Pedido findUltimoPedidoByUsuarioId(@Param("idUsuario") Integer idUsuario);
    
    // Total gastado por un usuario
    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE p.usuario.idUsuario = :idUsuario AND p.estado IN ('COMPLETADO', 'ENTREGADO', 'PAGADO')")
    Double findTotalGastadoByUsuarioId(@Param("idUsuario") Integer idUsuario);
    
    // ==================== MÉTODOS DERIVADOS CORREGIDOS (alternativos si los necesitas) ====================
    
    // Filtrar por cliente y estado - Versión con @Query para mayor claridad
    @Query("SELECT p FROM Pedido p WHERE p.usuario.idUsuario = :usuarioId AND p.estado = :estado")
    List<Pedido> findByUsuarioIdAndEstado(@Param("usuarioId") Integer usuarioId, @Param("estado") String estado);
    
    // Filtrar por cliente y rango de fechas - Versión con @Query
    @Query("SELECT p FROM Pedido p WHERE p.usuario.idUsuario = :usuarioId AND p.fechaPedido BETWEEN :inicio AND :fin")
    List<Pedido> findByUsuarioIdAndFechaPedidoBetween(@Param("usuarioId") Integer usuarioId, 
                                                     @Param("inicio") LocalDateTime inicio, 
                                                     @Param("fin") LocalDateTime fin);
    
    // ==================== NUEVOS MÉTODOS PARA DASHBOARD EN TIEMPO REAL ====================
    
    // Obtener estadísticas actualizadas del dashboard
    @Query("SELECT " +
           "COALESCE(SUM(CASE WHEN DATE(p.fechaPedido) = CURRENT_DATE THEN p.total ELSE 0 END), 0) as ventasHoy, " +
           "COUNT(p) as totalPedidos, " +
           "COALESCE(SUM(CASE WHEN DATE(p.fechaPedido) = CURRENT_DATE THEN 1 ELSE 0 END), 0) as pedidosHoy " +
           "FROM Pedido p")
    List<Object[]> getDashboardStats();
}