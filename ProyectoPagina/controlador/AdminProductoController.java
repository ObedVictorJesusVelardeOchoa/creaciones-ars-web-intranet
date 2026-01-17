package com.ProyectoPaginaWeb.ProyectoPagina.controlador;

import com.ProyectoPaginaWeb.ProyectoPagina.modelo.*;
import com.ProyectoPaginaWeb.ProyectoPagina.repositorio.ProductoRepository;
import com.ProyectoPaginaWeb.ProyectoPagina.repositorio.CategoryRepository;
import com.ProyectoPaginaWeb.ProyectoPagina.repositorio.TallaProductoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Controller
@RequestMapping("/admin/productos")
public class AdminProductoController {

    private final ProductoRepository productoRepository;
    private final CategoryRepository categoriaRepository;
    private final TallaProductoRepository tallaProductoRepository;

    public AdminProductoController(ProductoRepository productoRepository, 
                                 CategoryRepository categoriaRepository,
                                 TallaProductoRepository tallaProductoRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.tallaProductoRepository = tallaProductoRepository;
    }

    @PostConstruct
    public void inicializarCategorias() {
        // Verificar si ya hay categorías en la BD
        if (categoriaRepository.count() == 0) {
            System.out.println("Creando categorías fijas en la base de datos...");
            
            Category cat1 = new Category();
            cat1.setName("Zapatillas");
            categoriaRepository.save(cat1);
            
            Category cat2 = new Category();
            cat2.setName("Mocasines");
            categoriaRepository.save(cat2);
            
            Category cat3 = new Category();
            cat3.setName("Tacos");
            categoriaRepository.save(cat3);
            
            Category cat4 = new Category();
            cat4.setName("Botines");
            categoriaRepository.save(cat4);
            
            Category cat5 = new Category();
            cat5.setName("Sandalias");
            categoriaRepository.save(cat5);
            
            System.out.println("5 categorías creadas exitosamente");
        }
    }

    @GetMapping("/lista")
    public String listarProductos(Model model) {
        try {
            List<Producto> productos = productoRepository.findAllWithCategoriaSimple();
            
            for (Producto producto : productos) {
                if (producto.getTallas() != null) {
                    producto.getTallas().size();
                }
                // Asegurar que stockTotal esté actualizado
                producto.actualizarStockTotalCalculado();
            }
            
            long totalProductos = productoRepository.count();
            long stockTotal = tallaProductoRepository.sumTotalStock();
            long stockBajo = tallaProductoRepository.countProductosConStockBajo();
            long sinStock = tallaProductoRepository.countProductosSinStock();
            
            List<Category> categorias = categoriaRepository.findAll();
            
            model.addAttribute("totalProductos", totalProductos);
            model.addAttribute("stockTotal", stockTotal);
            model.addAttribute("stockBajo", stockBajo);
            model.addAttribute("sinStock", sinStock);
            model.addAttribute("productos", productos);
            model.addAttribute("producto", new Producto());
            model.addAttribute("categorias", categorias);
            
        } catch (Exception e) {
            System.err.println("Error al cargar productos: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar los productos: " + e.getMessage());
            model.addAttribute("productos", List.of());
            model.addAttribute("totalProductos", 0);
            model.addAttribute("stockTotal", 0);
            model.addAttribute("stockBajo", 0);
            model.addAttribute("sinStock", 0);
        }
        
        return "admin/lista-productos";
    }

    @PostMapping("/registro")
    public String guardarProducto(@ModelAttribute Producto producto,
                                  @RequestParam String talla,
                                  @RequestParam Integer stock,
                                  RedirectAttributes redirectAttributes) {
        
        if (productoRepository.existsByNumeroSerie(producto.getNumeroSerie())) {
            redirectAttributes.addFlashAttribute("error", "El número de serie ya existe");
            return "redirect:/admin/productos/lista";
        }
        
        try {
            if (producto.getCategoria() != null && producto.getCategoria().getId() != null) {
                Category categoriaBD = categoriaRepository.findById(producto.getCategoria().getId())
                        .orElseThrow(() -> new RuntimeException("Categoría no encontrada en BD con ID: " + producto.getCategoria().getId()));
                producto.setCategoria(categoriaBD);
            } else {
                throw new RuntimeException("Debe seleccionar una categoría válida");
            }
            
            // Establecer stockTotal inicial basado en la primera talla
            if (talla != null && !talla.trim().isEmpty() && stock != null) {
                producto.setStockTotal(stock);
            } else {
                producto.setStockTotal(0);
            }
            
            Producto productoGuardado = productoRepository.save(producto);
            
            // Crear la primera talla
            if (talla != null && !talla.trim().isEmpty() && stock != null) {
                TallaProducto tallaInicial = new TallaProducto();
                tallaInicial.setProducto(productoGuardado);
                tallaInicial.setTalla(talla.trim());
                tallaInicial.setStock(stock);
                tallaProductoRepository.save(tallaInicial);
            }
            
            redirectAttributes.addFlashAttribute("success", "Producto registrado exitosamente");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al registrar producto: " + e.getMessage());
        }
        
        return "redirect:/admin/productos/lista";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarProducto(@PathVariable("id") Integer id, 
                                    @ModelAttribute Producto productoActualizado,
                                    RedirectAttributes redirectAttributes) {
        try {
            Producto productoExistente = productoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ID de producto inválido: " + id));

            if (!productoExistente.getNumeroSerie().equals(productoActualizado.getNumeroSerie()) &&
                productoRepository.existsByNumeroSerie(productoActualizado.getNumeroSerie())) {
                redirectAttributes.addFlashAttribute("error", "El número de serie ya existe");
                return "redirect:/admin/productos/lista";
            }

            if (productoActualizado.getCategoria() != null && productoActualizado.getCategoria().getId() != null) {
                Category categoriaBD = categoriaRepository.findById(productoActualizado.getCategoria().getId())
                        .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
                productoExistente.setCategoria(categoriaBD);
            }
            
            productoExistente.setNumeroSerie(productoActualizado.getNumeroSerie());
            productoExistente.setModelo(productoActualizado.getModelo());
            productoExistente.setGenero(productoActualizado.getGenero());
            productoExistente.setPrecio(productoActualizado.getPrecio());

            productoRepository.save(productoExistente);
            redirectAttributes.addFlashAttribute("success", "Producto actualizado exitosamente");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al actualizar producto: " + e.getMessage());
        }
        
        return "redirect:/admin/productos/lista";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            if (productoRepository.existsById(id)) {
                List<TallaProducto> tallas = tallaProductoRepository.findByProductoId(id);
                tallaProductoRepository.deleteAll(tallas);
                productoRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("success", "Producto eliminado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar producto: " + e.getMessage());
        }
        
        return "redirect:/admin/productos/lista";
    }

    // NUEVO: Método para actualizar stockTotal cuando se manipulan tallas
    private void actualizarStockTotalProducto(Integer productoId) {
        try {
            Producto producto = productoRepository.findById(productoId).orElse(null);
            if (producto != null) {
                producto.actualizarStockTotalCalculado();
                productoRepository.save(producto);
            }
        } catch (Exception e) {
            System.err.println("Error actualizando stock total del producto " + productoId + ": " + e.getMessage());
        }
    }

    @PostMapping("/{productoId}/tallas/agregar")
    public String agregarTalla(@PathVariable Integer productoId, 
                                @RequestParam String talla, 
                                @RequestParam Integer stock,
                                RedirectAttributes redirectAttributes) {
        try {
            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
            
            if (tallaProductoRepository.existsByProductoIdAndTalla(productoId, talla)) {
                redirectAttributes.addFlashAttribute("error", "Esta talla ya existe para el producto");
                return "redirect:/admin/productos/lista";
            }
            
            TallaProducto nuevaTalla = new TallaProducto();
            nuevaTalla.setProducto(producto);
            nuevaTalla.setTalla(talla);
            nuevaTalla.setStock(stock);
            
            tallaProductoRepository.save(nuevaTalla);
            
            // ACTUALIZAR STOCK TOTAL DEL PRODUCTO
            actualizarStockTotalProducto(productoId);
            
            redirectAttributes.addFlashAttribute("success", "Talla agregada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al agregar talla: " + e.getMessage());
        }
        
        return "redirect:/admin/productos/lista";
    }

    @PostMapping("/tallas/actualizar/{tallaId}")
    public String actualizarTalla(@PathVariable Integer tallaId,
                                    @RequestParam Integer stock,
                                    RedirectAttributes redirectAttributes) {
        try {
            TallaProducto talla = tallaProductoRepository.findById(tallaId)
                    .orElseThrow(() -> new IllegalArgumentException("Talla no encontrada"));
            
            talla.setStock(stock);
            tallaProductoRepository.save(talla);
            
            // ACTUALIZAR STOCK TOTAL DEL PRODUCTO
            actualizarStockTotalProducto(talla.getProducto().getId());
            
            redirectAttributes.addFlashAttribute("success", "Stock actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar stock: " + e.getMessage());
        }
        
        return "redirect:/admin/productos/lista";
    }

    @GetMapping("/tallas/eliminar/{tallaId}")
    public String eliminarTalla(@PathVariable Integer tallaId, RedirectAttributes redirectAttributes) {
        try {
            TallaProducto talla = tallaProductoRepository.findById(tallaId)
                    .orElseThrow(() -> new IllegalArgumentException("Talla no encontrada"));
            
            Integer productoId = talla.getProducto().getId();
            tallaProductoRepository.delete(talla);
            
            // ACTUALIZAR STOCK TOTAL DEL PRODUCTO
            actualizarStockTotalProducto(productoId);
            
            redirectAttributes.addFlashAttribute("success", "Talla eliminada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar talla: " + e.getMessage());
        }
        
        return "redirect:/admin/productos/lista";
    }

    @GetMapping("/{productoId}/tallas")
    @ResponseBody
    public List<TallaProducto> obtenerTallasProducto(@PathVariable Integer productoId) {
        return tallaProductoRepository.findByProductoId(productoId);
    }
}