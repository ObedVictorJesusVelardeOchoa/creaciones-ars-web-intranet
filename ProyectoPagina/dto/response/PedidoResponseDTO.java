package com.ProyectoPaginaWeb.ProyectoPagina.dto.response;

import java.time.LocalDateTime;
import java.util.List;

// DTO para respuesta de pedido
public class PedidoResponseDTO {
    private Integer id;
    private Integer usuarioId;
    private String nombreUsuario;
    private String dniUsuario;
    private LocalDateTime fechaPedido;
    private String estado;
    private Double total;
    private String direccionEnvio;
    private String metodoPago;
    private LocalDateTime fechaActualizacion;
    private List<DetallePedidoResponseDTO> detalles;
    
    // Constructores
    public PedidoResponseDTO() {}
    
    // Getters y Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getUsuarioId() {
        return usuarioId;
    }
    
    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }
    
    public String getNombreUsuario() {
        return nombreUsuario;
    }
    
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
    
    public String getDniUsuario() {
        return dniUsuario;
    }
    
    public void setDniUsuario(String dniUsuario) {
        this.dniUsuario = dniUsuario;
    }
    
    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }
    
    public void setFechaPedido(LocalDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public Double getTotal() {
        return total;
    }
    
    public void setTotal(Double total) {
        this.total = total;
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
    
    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
    
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
    
    public List<DetallePedidoResponseDTO> getDetalles() {
        return detalles;
    }
    
    public void setDetalles(List<DetallePedidoResponseDTO> detalles) {
        this.detalles = detalles;
    }
}
