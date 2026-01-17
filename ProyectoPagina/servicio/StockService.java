package com.ProyectoPaginaWeb.ProyectoPagina.servicio;

import com.ProyectoPaginaWeb.ProyectoPagina.modelo.Producto;
import com.ProyectoPaginaWeb.ProyectoPagina.modelo.TallaProducto;
import com.ProyectoPaginaWeb.ProyectoPagina.repositorio.ProductoRepository;
import com.ProyectoPaginaWeb.ProyectoPagina.repositorio.TallaProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StockService {
    
    @Autowired
    private TallaProductoRepository tallaProductoRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    public boolean verificarStock(Integer productoId, String talla, Integer cantidad) {
        Optional<TallaProducto> tallaProducto = tallaProductoRepository.findByProductoIdAndTalla(productoId, talla);
        return tallaProducto.isPresent() && tallaProducto.get().getStock() >= cantidad;
    }
    
    // MÉTODO ACTUALIZADO - Reducir stock y actualizar Producto.stockTotal
    public boolean reducirStock(Integer productoId, String talla, Integer cantidad) {
        try {
            // 1. Reducir stock de la talla específica usando el método del repositorio
            int filasActualizadas = tallaProductoRepository.reducirStock(productoId, talla, cantidad);
            
            if (filasActualizadas > 0) {
                // 2. Actualizar stockTotal del Producto manualmente
                Producto producto = productoRepository.findById(productoId).orElse(null);
                if (producto != null) {
                    // Calcular el nuevo stock total
                    Integer nuevoStockTotal = producto.getStockTotal() - cantidad;
                    producto.setStockTotal(nuevoStockTotal);
                    productoRepository.save(producto);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // NUEVO MÉTODO: Actualizar stock completo (talla específica + stockTotal del producto)
    @Transactional
    public void actualizarStockCompleto(Integer productoId, String talla, Integer cantidad) {
        System.out.println("Actualizando stock completo para productoId: " + productoId + 
                        ", talla: " + talla + ", cantidad: " + cantidad);
        
        // Buscar la talla específica
        Optional<TallaProducto> tallaProductoOpt = tallaProductoRepository
                .findByProductoIdAndTalla(productoId, talla);
        
        // Buscar el producto
        Optional<Producto> productoOpt = productoRepository.findById(productoId);
        
        if (tallaProductoOpt.isPresent() && productoOpt.isPresent()) {
            TallaProducto tallaProducto = tallaProductoOpt.get();
            Producto producto = productoOpt.get();
            
            System.out.println("Stock antes - Talla: " + tallaProducto.getStock() + 
                            ", Total Producto: " + producto.getStockTotal());
            
            // Verificar que hay suficiente stock
            if (tallaProducto.getStock() < cantidad) {
                throw new RuntimeException("Stock insuficiente para la talla: " + talla + 
                                        ". Stock disponible: " + tallaProducto.getStock());
            }
            
            // Actualizar stock de la talla específica
            tallaProducto.setStock(tallaProducto.getStock() - cantidad);
            tallaProductoRepository.save(tallaProducto);
            
            // Actualizar stock total del producto
            producto.setStockTotal(producto.getStockTotal() - cantidad);
            productoRepository.save(producto);
            
            System.out.println("Stock después - Talla: " + tallaProducto.getStock() + 
                            ", Total Producto: " + producto.getStockTotal());
            
            // Forzar sincronización inmediata con la base de datos
            productoRepository.flush();
        } else {
            throw new RuntimeException("No se encontró producto o talla. ProductoID: " + 
                                    productoId + ", Talla: " + talla);
        }
    }
    
    public List<TallaProducto> obtenerTallasDisponibles(Integer productoId) {
        return tallaProductoRepository.findByProductoId(productoId);
    }
    
    public Optional<TallaProducto> obtenerTallaProducto(Integer productoId, String talla) {
        return tallaProductoRepository.findByProductoIdAndTalla(productoId, talla);
    }
    
    public boolean existenTallasParaProducto(Integer productoId) {
        return tallaProductoRepository.existsByProductoId(productoId);
    }
}