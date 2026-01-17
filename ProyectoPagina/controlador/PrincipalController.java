package com.ProyectoPaginaWeb.ProyectoPagina.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ProyectoPaginaWeb.ProyectoPagina.servicio.ProductoService;

@Controller
public class PrincipalController {

    @Autowired
    private ProductoService productoService;

    @GetMapping("/principal")
    public String mostrarPrincipal(Model model) {
        model.addAttribute("productos", productoService.obtenerTodosProductos());
        return "principal";
    }

    @GetMapping("/ventas")
    public String mostrarVenta(Model model) {
        model.addAttribute("productos", productoService.obtenerTodosProductos());
        return "ventas"; 
    }
    
    @GetMapping("/carrito")
    public String carrito() {
        return "carrito";
    }
    
    @GetMapping("/info")
    public String info() {
        return "info";
    }
    
    @GetMapping("/ofertas")
    public String ofertas() {
        return "ofertas";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}