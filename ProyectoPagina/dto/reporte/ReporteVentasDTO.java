package com.ProyectoPaginaWeb.ProyectoPagina.dto.reporte;

import java.time.LocalDate;
import java.util.List;

// DTO para reporte de ventas - CORREGIDO CON List<Object[]>
public class ReporteVentasDTO {
    private String periodo;
    private Double totalVentas;
    private LocalDate fechaGeneracion;
    private Long totalPedidos;
    private List<Object[]> pedidosPorEstado;  // Cambiado a Object[]
    
    // Constructores
    public ReporteVentasDTO() {
        this.fechaGeneracion = LocalDate.now();
    }
    
    public ReporteVentasDTO(String periodo, Double totalVentas, Long totalPedidos) {
        this();
        this.periodo = periodo;
        this.totalVentas = totalVentas;
        this.totalPedidos = totalPedidos;
    }
    
    // Getters y Setters
    public String getPeriodo() {
        return periodo;
    }
    
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }
    
    public Double getTotalVentas() {
        return totalVentas;
    }
    
    public void setTotalVentas(Double totalVentas) {
        this.totalVentas = totalVentas;
    }
    
    public LocalDate getFechaGeneracion() {
        return fechaGeneracion;
    }
    
    public void setFechaGeneracion(LocalDate fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
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
}