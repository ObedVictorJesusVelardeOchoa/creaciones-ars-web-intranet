package com.ProyectoPaginaWeb.ProyectoPagina.repositorio;

import com.ProyectoPaginaWeb.ProyectoPagina.modelo.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {
    // Encontrar detalles por pedido
    List<DetallePedido> findByPedidoId(Integer pedidoId);
    // Productos más vendidos
    @Query("SELECT dp.producto, SUM(dp.cantidad) as totalVendido FROM DetallePedido dp JOIN dp.pedido p WHERE p.estado = 'ENTREGADO' GROUP BY dp.producto ORDER BY totalVendido DESC")
    List<Object[]> findProductosMasVendidos(@Param("limite") int limite);
}