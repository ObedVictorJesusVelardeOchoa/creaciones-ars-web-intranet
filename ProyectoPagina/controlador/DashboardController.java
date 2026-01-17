package com.ProyectoPaginaWeb.ProyectoPagina.controlador;

import com.ProyectoPaginaWeb.ProyectoPagina.dto.reporte.DashboardDTO;
import com.ProyectoPaginaWeb.ProyectoPagina.servicio.PedidoService;
import com.ProyectoPaginaWeb.ProyectoPagina.servicio.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/dashboard")
public class DashboardController {

    @Autowired
    private PedidoService pedidoService;
    
    @Autowired
    private ProductoService productoService;

    // Vista del dashboard
    @GetMapping("")
    public String dashboard(Model model) {
        try {
            DashboardDTO dashboard = pedidoService.obtenerEstadisticasDashboard();
            model.addAttribute("dashboard", dashboard);
            return "admin/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar dashboard: " + e.getMessage());
            return "admin/dashboard";
        }
    }

    // API para obtener datos del dashboard (AJAX)
    @GetMapping("/datos")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDatosDashboard() {
        Map<String, Object> datos = new HashMap<>();
        
        try {
            DashboardDTO dashboard = pedidoService.obtenerEstadisticasDashboard();
            
            datos.put("ventasHoy", dashboard.getVentasHoy());
            datos.put("totalPedidos", dashboard.getTotalPedidos());
            datos.put("pedidosPorEstado", dashboard.getPedidosPorEstado());
            datos.put("cantidadProductosStockBajo", dashboard.getCantidadProductosStockBajo());
            datos.put("productosStockBajo", dashboard.getProductosStockBajo());
            datos.put("ultimosPedidos", dashboard.getUltimosPedidos());
            datos.put("success", true);
            
        } catch (Exception e) {
            datos.put("success", false);
            datos.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(datos);
    }
}