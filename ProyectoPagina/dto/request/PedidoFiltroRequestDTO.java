package com.ProyectoPaginaWeb.ProyectoPagina.dto.request;

import java.time.LocalDate;
import java.util.List;

// DTO para filtrar pedidos
public class PedidoFiltroRequestDTO {
    private Integer clienteId;
    private String estado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    
    // Constructores
    public PedidoFiltroRequestDTO() {}
    
    public PedidoFiltroRequestDTO(Integer clienteId, String estado, LocalDate fechaInicio, LocalDate fechaFin) {
        this.clienteId = clienteId;
        this.estado = estado;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }
    
    // Getters y Setters
    public Integer getClienteId() {
        return clienteId;
    }
    
    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }
    
    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    
    public LocalDate getFechaFin() {
        return fechaFin;
    }
    
    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }
}

// DTO para crear un pedido
