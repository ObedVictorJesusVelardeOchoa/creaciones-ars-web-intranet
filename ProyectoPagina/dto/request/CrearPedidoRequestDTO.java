package com.ProyectoPaginaWeb.ProyectoPagina.dto.request;

import java.util.List;

public class CrearPedidoRequestDTO {
    private Integer usuarioId;
    private String direccionEnvio;
    private String metodoPago;
    private List<ItemPedidoRequestDTO> items;
    
    // Constructores
    public CrearPedidoRequestDTO() {}
    
    // Getters y Setters
    public Integer getUsuarioId() {
        return usuarioId;
    }
    
    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }
    
    public String getDireccionEnvio() {
        return direccionEnvio;
    }
    
    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }
    
    public String getMetodoPago() {
        return metodoPago;
    }
    
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
    
    public List<ItemPedidoRequestDTO> getItems() {
        return items;
    }
    
    public void setItems(List<ItemPedidoRequestDTO> items) {
        this.items = items;
    }
}