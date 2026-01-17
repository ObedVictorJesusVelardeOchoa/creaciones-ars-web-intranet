package com.ProyectoPaginaWeb.ProyectoPagina.dto.response;

public class DetallePedidoResponseDTO {
    private Integer id;
    private Integer productoId;
    private String nombreProducto;
    private String numeroSerieProducto;
    private Integer cantidad;
    private Double precioUnitario;
    private String tallaSeleccionada;
    private Double subtotal;
    
    // Constructores
    public DetallePedidoResponseDTO() {}
    
    // Getters y Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getProductoId() {
        return productoId;
    }
    
    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }
    
    public String getNombreProducto() {
        return nombreProducto;
    }
    
    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }
    
    public String getNumeroSerieProducto() {
        return numeroSerieProducto;
    }
    
    public void setNumeroSerieProducto(String numeroSerieProducto) {
        this.numeroSerieProducto = numeroSerieProducto;
    }
    
    public Integer getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    
    public Double getPrecioUnitario() {
        return precioUnitario;
    }
    
    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    
    public String getTallaSeleccionada() {
        return tallaSeleccionada;
    }
    
    public void setTallaSeleccionada(String tallaSeleccionada) {
        this.tallaSeleccionada = tallaSeleccionada;
    }
    
    public Double getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}