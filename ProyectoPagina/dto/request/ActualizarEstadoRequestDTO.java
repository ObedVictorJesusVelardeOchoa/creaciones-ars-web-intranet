package com.ProyectoPaginaWeb.ProyectoPagina.dto.request;

public class ActualizarEstadoRequestDTO {
    private String nuevoEstado;
    
    // Constructores
    public ActualizarEstadoRequestDTO() {}
    
    // Getters y Setters
    public String getNuevoEstado() {
        return nuevoEstado;
    }
    
    public void setNuevoEstado(String nuevoEstado) {
        this.nuevoEstado = nuevoEstado;
    }
}