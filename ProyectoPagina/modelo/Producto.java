package com.ProyectoPaginaWeb.ProyectoPagina.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Entity
@Table(name = "producto")
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El número de serie es obligatorio")
    @Column(unique = true, nullable = false)
    private String numeroSerie;

    @NotBlank(message = "El género es obligatorio")
    private String genero;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(min = 2, max = 100, message = "El modelo debe tener entre 2 y 100 caracteres")
    private String modelo;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private Double precio;

    @NotNull(message = "La categoría es obligatoria")
    @ManyToOne 
    @JoinColumn(name = "categoria_id") 
    private Category categoria;
    
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TallaProducto> tallas;
    
    // NUEVO: Campo stockTotal persistido
    @Column(name = "stock_total", nullable = false)
    private Integer stockTotal;
    
    // Constructor vacío
    public Producto() {
        this.tallas = new ArrayList<>();
        this.stockTotal = 0; // Inicializar a 0
    }
    
    // Getters y Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getNumeroSerie() {
        return numeroSerie;
    }
    
    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }
    
    public String getGenero() {
        return genero;
    }
    
    public void setGenero(String genero) {
        this.genero = genero;
    }
    
    public String getModelo() {
        return modelo;
    }
    
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }
    
    public Double getPrecio() {
        return precio;
    }
    
    public void setPrecio(Double precio) {
        this.precio = precio;
    }
    
    public Category getCategoria() {
        return categoria;
    }
    
    public void setCategoria(Category categoria) {
        this.categoria = categoria;
    }
    
    public List<TallaProducto> getTallas() {
        return tallas;
    }
    
    public void setTallas(List<TallaProducto> tallas) {
        this.tallas = tallas;
        // Establecer la relación inversa
        if (tallas != null) {
            for (TallaProducto talla : tallas) {
                talla.setProducto(this);
            }
        }
        // Actualizar stockTotal al asignar nuevas tallas
        actualizarStockTotalCalculado();
    }
    
    // NUEVO: Getter y Setter para stockTotal persistido
    public Integer getStockTotal() {
        return stockTotal;
    }
    
    public void setStockTotal(Integer stockTotal) {
        this.stockTotal = stockTotal;
    }
    
    // Método auxiliar para calcular stock total basado en tallas
    public void actualizarStockTotalCalculado() {
        if (tallas == null || tallas.isEmpty()) {
            this.stockTotal = 0;
        } else {
            this.stockTotal = tallas.stream().mapToInt(TallaProducto::getStock).sum();
        }
    }
    
    // Método auxiliar calculado (para compatibilidad)
    public Integer getStockTotalCalculado() {
        if (tallas == null || tallas.isEmpty()) return 0;
        return tallas.stream().mapToInt(TallaProducto::getStock).sum();
    }
    
    // Método corregido - devuelve lista de Strings
    public List<String> getTallasDisponibles() {
        if (tallas == null || tallas.isEmpty()) return new ArrayList<>();
        return tallas.stream()
                .filter(tp -> tp.getStock() > 0)
                .map(TallaProducto::getTalla)
                .collect(Collectors.toList());
    }
    
    // Método para agregar una talla
    public void agregarTalla(TallaProducto talla) {
        if (this.tallas == null) {
            this.tallas = new ArrayList<>();
        }
        talla.setProducto(this);
        this.tallas.add(talla);
        // Actualizar stockTotal
        this.stockTotal = this.getStockTotalCalculado();
    }
}