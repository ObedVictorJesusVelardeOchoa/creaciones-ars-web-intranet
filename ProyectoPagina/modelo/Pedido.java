package com.ProyectoPaginaWeb.ProyectoPagina.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "pedido")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(name = "fecha_pedido", nullable = false)
    private LocalDateTime fechaPedido;
    
    @Column(name = "estado", nullable = false, length = 20)
    private String estado; // PENDIENTE, PROCESANDO, ENVIADO, ENTREGADO, CANCELADO
    
    @Column(name = "total", nullable = false)
    private Double total;
    
    @Column(name = "direccion_envio", nullable = false, length = 200)
    private String direccionEnvio;
    
    @Column(name = "metodo_pago", nullable = false, length = 50)
    private String metodoPago;
    
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetallePedido> detalles = new ArrayList<>();
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    // Constructores
    public Pedido() {
        this.fechaPedido = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        this.estado = "PENDIENTE";
    }
    
    public Pedido(Usuario usuario, String direccionEnvio, String metodoPago) {
        this();
        this.usuario = usuario;
        this.direccionEnvio = direccionEnvio;
        this.metodoPago = metodoPago;
    }
    
    // Getters y Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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
        this.fechaActualizacion = LocalDateTime.now();
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
    
    public List<DetallePedido> getDetalles() {
        return detalles;
    }
    
    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles;
    }
    
    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
    
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
    
    // Método para calcular total
    public void calcularTotal() {
        this.total = detalles.stream()
                .mapToDouble(DetallePedido::getSubtotal)
                .sum();
    }
    
    // Método para agregar detalle
    public void agregarDetalle(DetallePedido detalle) {
        detalle.setPedido(this);
        this.detalles.add(detalle);
    }
}