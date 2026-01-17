package com.ProyectoPaginaWeb.ProyectoPagina.servicio;

import com.ProyectoPaginaWeb.ProyectoPagina.modelo.Producto;
import com.ProyectoPaginaWeb.ProyectoPagina.repositorio.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {
    
    @Autowired
    private ProductoRepository productoRepository;
    
    public List<Producto> obtenerTodosProductos() {
        return productoRepository.findAll();
    }
    
    public Optional<Producto> obtenerProductoPorId(Integer id) {
        return productoRepository.findById(id);
    }
    
    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }
    
    public void eliminarProducto(Integer id) {
        productoRepository.deleteById(id);
    }
    
    // NUEVO: Método para actualizar stock
    @Transactional
    public void actualizarStockProducto(Integer productoId, Integer cantidadVendida) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        // Verificar stock suficiente
        if (producto.getStockTotal() < cantidadVendida) {
            throw new RuntimeException("Stock insuficiente para el producto: " + producto.getModelo());
        }
        
        // Actualizar stock total
        producto.setStockTotal(producto.getStockTotal() - cantidadVendida);
        productoRepository.save(producto);
    }
    
    // NUEVO: Método para obtener productos con stock bajo
    public List<Producto> obtenerProductosConStockBajo(Integer limiteStock) {
        return productoRepository.findAll().stream()
                .filter(p -> p.getStockTotal() <= limiteStock)
                .toList();
    }
}