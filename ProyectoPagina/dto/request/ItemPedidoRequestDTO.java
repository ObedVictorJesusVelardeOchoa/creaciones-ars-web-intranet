package com.ProyectoPaginaWeb.ProyectoPagina.dto.request;

public class ItemPedidoRequestDTO {
    private Integer productoId;
    private Integer cantidad;
    private String talla;
    
    // Constructores
    public ItemPedidoRequestDTO() {}
    
    // Getters y Setters
    public Integer getProductoId() {
        return productoId;
    }
    
    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }
    
    public Integer getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    
    public String getTalla() {
        return talla;
    }
    
    public void setTalla(String talla) {
        this.talla = talla;
    }
}