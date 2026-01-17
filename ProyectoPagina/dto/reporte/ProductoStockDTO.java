package com.ProyectoPaginaWeb.ProyectoPagina.dto.reporte;

import java.util.List;
import java.util.Map;

public class ProductoStockDTO {
    private Integer id;
    private String numeroSerie;
    private String modelo;
    private String categoria;
    private Double precio;
    private Integer stockTotal;
    private List<String> tallasDisponibles;
    private List<Map<String, Object>> detalleTallas;
    
    // Constructores
    public ProductoStockDTO() {}
    
    public ProductoStockDTO(Integer id, String numeroSerie, String modelo, String categoria, 
                           Double precio, Integer stockTotal, List<String> tallasDisponibles) {
        this.id = id;
        this.numeroSerie = numeroSerie;
        this.modelo = modelo;
        this.categoria = categoria;
        this.precio = precio;
        this.stockTotal = stockTotal;
        this.tallasDisponibles = tallasDisponibles;
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
    
    public String getModelo() {
        return modelo;
    }
    
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }
    
    public String getCategoria() {
        return categoria;
    }
    
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    
    public Double getPrecio() {
        return precio;
    }
    
    public void setPrecio(Double precio) {
        this.precio = precio;
    }
    
    public Integer getStockTotal() {
        return stockTotal;
    }
    
    public void setStockTotal(Integer stockTotal) {
        this.stockTotal = stockTotal;
    }
    
    public List<String> getTallasDisponibles() {
        return tallasDisponibles;
    }
    
    public void setTallasDisponibles(List<String> tallasDisponibles) {
        this.tallasDisponibles = tallasDisponibles;
    }
    
    public List<Map<String, Object>> getDetalleTallas() {
        return detalleTallas;
    }
    
    public void setDetalleTallas(List<Map<String, Object>> detalleTallas) {
        this.detalleTallas = detalleTallas;
    }
}