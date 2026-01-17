package com.ProyectoPaginaWeb.ProyectoPagina.controlador;

import com.ProyectoPaginaWeb.ProyectoPagina.dto.reporte.*;
import com.ProyectoPaginaWeb.ProyectoPagina.servicio.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {
    
    @Autowired
    private PedidoService pedidoService;
    
    // 1. Reporte de ventas diarias, semanales, mensuales
    @GetMapping("/ventas/{periodo}")
    public ResponseEntity<ReporteVentasDTO> getReporteVentas(@PathVariable String periodo) {
        return ResponseEntity.ok(pedidoService.generarReporteVentas(periodo));
    }
    
    // 2. Reporte de stock completo
    @GetMapping("/stock")
    public ResponseEntity<List<ProductoStockDTO>> getReporteStock() {
        return ResponseEntity.ok(pedidoService.generarReporteStock());
    }
    
    // 3. Dashboard con estadísticas
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboard() {
        return ResponseEntity.ok(pedidoService.obtenerEstadisticasDashboard());
    }
}