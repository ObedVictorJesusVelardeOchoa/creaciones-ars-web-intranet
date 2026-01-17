package com.ProyectoPaginaWeb.ProyectoPagina.controlador;

import com.ProyectoPaginaWeb.ProyectoPagina.modelo.Producto;
import com.ProyectoPaginaWeb.ProyectoPagina.modelo.TallaProducto;
import com.ProyectoPaginaWeb.ProyectoPagina.repositorio.ProductoRepository;
import com.ProyectoPaginaWeb.ProyectoPagina.repositorio.TallaProductoRepository;
import com.ProyectoPaginaWeb.ProyectoPagina.servicio.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {
    
    @Autowired
    private StockService stockService;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TallaProductoRepository tallaProductoRepository;

    // ========== ENDPOINTS PARA GESTIÓN DE STOCK ==========

    /**
     * Endpoint para obtener tallas disponibles de un producto
     */
    @GetMapping("/productos/{productoId}/tallas")
    public ResponseEntity<List<TallaProducto>> obtenerTallasDisponibles(@PathVariable Integer productoId) {
        List<TallaProducto> tallas = stockService.obtenerTallasDisponibles(productoId);
        return ResponseEntity.ok(tallas);
    }

    /**
     * Endpoint para verificar stock antes de agregar al carrito
     */
    @PostMapping("/carrito/verificar-stock")
    public ResponseEntity<?> verificarStock(@RequestBody VerificarStockRequest request) {
        boolean stockDisponible = stockService.verificarStock(
            request.getProductoId(), 
            request.getTalla(), 
            request.getCantidad()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("stockDisponible", stockDisponible);
        
        if (!stockDisponible) {
            // Obtener información del stock disponible
            Optional<TallaProducto> tallaProducto = stockService.obtenerTallaProducto(
                request.getProductoId(), request.getTalla());
            
            if (tallaProducto.isPresent()) {
                response.put("mensaje", "Stock insuficiente. Solo quedan " + 
                            tallaProducto.get().getStock() + " unidades disponibles.");
            } else {
                response.put("mensaje", "Talla no disponible para este producto.");
            }
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para finalizar compra y reducir stock
     */
    @PostMapping("/carrito/finalizar-compra")
    public ResponseEntity<?> finalizarCompra(@RequestBody List<ProductoCarrito> productos) {
        // Primero verificar stock para todos los productos
        for (ProductoCarrito producto : productos) {
            if (!stockService.verificarStock(producto.getProductoId(), producto.getTalla(), producto.getCantidad())) {
                return ResponseEntity.badRequest().body("Stock insuficiente para: " + producto.getNombre());
            }
        }
        
        // Reducir stock para todos los productos
        for (ProductoCarrito producto : productos) {
            boolean exito = stockService.reducirStock(
                producto.getProductoId(), 
                producto.getTalla(), 
                producto.getCantidad()
            );
            
            if (!exito) {
                return ResponseEntity.badRequest().body("Error al actualizar stock para: " + producto.getNombre());
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Compra realizada exitosamente");
        response.put("productosProcesados", productos.size());
        
        return ResponseEntity.ok(response);
    }

    // ========== ENDPOINTS PARA GESTIÓN DE TALLAS ==========

    /**
     * Endpoint para agregar una nueva talla a un producto
     */
    @PostMapping("/productos/{productoId}/tallas")
    public ResponseEntity<?> agregarTalla(@PathVariable Integer productoId, 
                                            @RequestBody TallaRequest request) {
        try {
            Optional<Producto> productoOpt = productoRepository.findById(productoId);
            if (productoOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Producto no encontrado");
            }

            // Verificar si ya existe esta talla para el producto
            Optional<TallaProducto> tallaExistente = tallaProductoRepository
                .findByProductoIdAndTalla(productoId, request.getTalla());
            
            if (tallaExistente.isPresent()) {
                return ResponseEntity.badRequest().body("Esta talla ya existe para el producto");
            }

            TallaProducto nuevaTalla = new TallaProducto();
            nuevaTalla.setProducto(productoOpt.get());
            nuevaTalla.setTalla(request.getTalla());
            nuevaTalla.setStock(request.getStock());

            TallaProducto tallaGuardada = tallaProductoRepository.save(nuevaTalla);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Talla agregada exitosamente");
            response.put("talla", tallaGuardada);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error agregando talla: " + e.getMessage());
        }
    }

    /**
     * Endpoint para actualizar el stock de una talla
     */
    @PutMapping("/tallas/{tallaId}/stock")
    public ResponseEntity<?> actualizarStock(@PathVariable Integer tallaId, 
                                            @RequestBody ActualizarStockRequest request) {
        try {
            Optional<TallaProducto> tallaOpt = tallaProductoRepository.findById(tallaId);
            if (tallaOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Talla no encontrada");
            }

            TallaProducto talla = tallaOpt.get();
            talla.setStock(request.getStock());
            tallaProductoRepository.save(talla);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Stock actualizado exitosamente");
            response.put("talla", talla);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error actualizando stock: " + e.getMessage());
        }
    }

    /**
     * Endpoint para eliminar una talla
     */
    @DeleteMapping("/tallas/{tallaId}")
    public ResponseEntity<?> eliminarTalla(@PathVariable Integer tallaId) {
        try {
            Optional<TallaProducto> tallaOpt = tallaProductoRepository.findById(tallaId);
            if (tallaOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Talla no encontrada");
            }

            TallaProducto talla = tallaOpt.get();
            tallaProductoRepository.delete(talla);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Talla eliminada exitosamente");
            response.put("tallaEliminada", tallaId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error eliminando talla: " + e.getMessage());
        }
    }

    // ========== CLASES INTERNAS PARA REQUEST BODIES ==========

    public static class VerificarStockRequest {
        private Integer productoId;
        private String talla;
        private Integer cantidad;
        
        // Getters y Setters
        public Integer getProductoId() { return productoId; }
        public void setProductoId(Integer productoId) { this.productoId = productoId; }
        public String getTalla() { return talla; }
        public void setTalla(String talla) { this.talla = talla; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    }
    
    public static class ProductoCarrito {
        private Integer productoId;
        private String nombre;
        private String talla;
        private Integer cantidad;
        private Double precio;
        
        // Getters y Setters
        public Integer getProductoId() { return productoId; }
        public void setProductoId(Integer productoId) { this.productoId = productoId; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getTalla() { return talla; }
        public void setTalla(String talla) { this.talla = talla; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
        public Double getPrecio() { return precio; }
        public void setPrecio(Double precio) { this.precio = precio; }
    }

    public static class TallaRequest {
        private String talla;
        private Integer stock;
        
        // Getters y Setters
        public String getTalla() { return talla; }
        public void setTalla(String talla) { this.talla = talla; }
        public Integer getStock() { return stock; }
        public void setStock(Integer stock) { this.stock = stock; }
    }

    public static class ActualizarStockRequest {
        private Integer stock;
        
        // Getters y Setters
        public Integer getStock() { return stock; }
        public void setStock(Integer stock) { this.stock = stock; }
    }
}