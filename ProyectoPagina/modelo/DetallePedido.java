package com.ProyectoPaginaWeb.ProyectoPagina.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;
    
    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario;
    
    @Column(name = "talla_seleccionada", nullable = false, length = 10)
    private String tallaSeleccionada;
    
    @Column(name = "subtotal", nullable = false)
    private Double subtotal;
    
    // Constructores
    public DetallePedido() {}
    
    public DetallePedido(Producto producto, Integer cantidad, String tallaSeleccionada) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.tallaSeleccionada = tallaSeleccionada;
        this.precioUnitario = producto.getPrecio();
        calcularSubtotal();
    }
    
    // Getters y Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Pedido getPedido() {
        return pedido;
    }
    
    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
    
    public Producto getProducto() {
        return producto;
    }
    
    public void setProducto(Producto producto) {
        this.producto = producto;
        this.precioUnitario = producto.getPrecio();
        calcularSubtotal();
    }
    
    public Integer getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        calcularSubtotal();
    }
    
    public Double getPrecioUnitario() {
        return precioUnitario;
    }
    
    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
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
    
    // Método para calcular subtotal
    public void calcularSubtotal() {
        this.subtotal = this.cantidad * this.precioUnitario;
    }
}