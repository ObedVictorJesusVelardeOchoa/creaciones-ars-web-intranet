package com.ProyectoPaginaWeb.ProyectoPagina.config;

import com.ProyectoPaginaWeb.ProyectoPagina.modelo.Producto;
import com.ProyectoPaginaWeb.ProyectoPagina.modelo.TallaProducto;
import com.ProyectoPaginaWeb.ProyectoPagina.repositorio.ProductoRepository;
import com.ProyectoPaginaWeb.ProyectoPagina.repositorio.TallaProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private TallaProductoRepository tallaProductoRepository;

    @Override
    public void run(String... args) throws Exception {
        // No migramos automáticamente, solo informamos
        System.out.println("=== INFORMACIÓN DE MIGRACIÓN ===");
        System.out.println("Total de productos: " + productoRepository.count());
        System.out.println("Total de tallas registradas: " + tallaProductoRepository.count());
        System.out.println("=== Para migrar datos, ejecutar manualmente ===");
    }

    // Método para migración manual (puedes llamarlo desde un controlador)
    public void migrarDatosManual() {
        if (tallaProductoRepository.count() == 0) {
            System.out.println("Iniciando migración manual de datos...");
            
            List<Producto> productos = productoRepository.findAll();
            int migrados = 0;
            
            for (Producto producto : productos) {
                try {
                    // Aquí necesitamos obtener talla y stock de otra manera
                    // Por ahora, creamos una talla por defecto
                    TallaProducto tallaProducto = new TallaProducto();
                    tallaProducto.setProducto(producto);
                    tallaProducto.setTalla("38"); // Talla por defecto
                    tallaProducto.setStock(10);   // Stock por defecto
                    
                    tallaProductoRepository.save(tallaProducto);
                    migrados++;
                    
                    System.out.println("Migrado producto: " + producto.getModelo());
                } catch (Exception e) {
                    System.err.println("Error migrando producto " + producto.getId() + ": " + e.getMessage());
                }
            }
            
            System.out.println("Migración manual completada. Total: " + migrados);
        }
    }
}