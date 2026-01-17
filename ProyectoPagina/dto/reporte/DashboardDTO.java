package com.ProyectoPaginaWeb.ProyectoPagina.dto.reporte;

import java.util.List;
import java.util.Map;

public class DashboardDTO {
    private Double ventasHoy;
    private Long totalPedidos;
    private List<Object[]> pedidosPorEstado;  // Cambiado a Object[]
    private Integer cantidadProductosStockBajo;
    private List<Map<String, Object>> productosStockBajo;
    private List<PedidoResumenDTO> ultimosPedidos;
    
    // Constructores
    public DashboardDTO() {}
    
    // Getters y Setters
    public Double getVentasHoy() {
        return ventasHoy;
    }
    
    public void setVentasHoy(Double ventasHoy) {
        this.ventasHoy = ventasHoy;
    }
    
    public Long getTotalPedidos() {
        return totalPedidos;
    }
    
    public void setTotalPedidos(Long totalPedidos) {
        this.totalPedidos = totalPedidos;
    }
    
    // Cambiado: Ahora acepta List<Object[]>
    public List<Object[]> getPedidosPorEstado() {
        return pedidosPorEstado;
    }
    
    public void setPedidosPorEstado(List<Object[]> pedidosPorEstado) {
        this.pedidosPorEstado = pedidosPorEstado;
    }
    
    public Integer getCantidadProductosStockBajo() {
        return cantidadProductosStockBajo;
    }
    
    public void setCantidadProductosStockBajo(Integer cantidadProductosStockBajo) {
        this.cantidadProductosStockBajo = cantidadProductosStockBajo;
    }
    
    public List<Map<String, Object>> getProductosStockBajo() {
        return productosStockBajo;
    }
    
    public void setProductosStockBajo(List<Map<String, Object>> productosStockBajo) {
        this.productosStockBajo = productosStockBajo;
    }
    
    public List<PedidoResumenDTO> getUltimosPedidos() {
        return ultimosPedidos;
    }
    
    public void setUltimosPedidos(List<PedidoResumenDTO> ultimosPedidos) {
        this.ultimosPedidos = ultimosPedidos;
    }
}