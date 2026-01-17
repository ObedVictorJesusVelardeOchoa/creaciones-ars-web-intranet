package com.ProyectoPaginaWeb.ProyectoPagina.controlador;

import com.ProyectoPaginaWeb.ProyectoPagina.dto.request.*;
import com.ProyectoPaginaWeb.ProyectoPagina.dto.reporte.DashboardDTO;
import com.ProyectoPaginaWeb.ProyectoPagina.dto.response.*;
import com.ProyectoPaginaWeb.ProyectoPagina.servicio.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    // 1. Historial de pedidos por cliente
    @GetMapping("/historial/{clienteId}")
    public ResponseEntity<List<PedidoResponseDTO>> getHistorialCliente(@PathVariable Integer clienteId) {
        return ResponseEntity.ok(pedidoService.obtenerHistorialPorCliente(clienteId));
    }

    // 2. Buscar y filtrar pedidos usando DTO
    @PostMapping("/filtrar")
    public ResponseEntity<List<PedidoResponseDTO>> filtrarPedidos(@RequestBody PedidoFiltroRequestDTO filtro) {
        return ResponseEntity.ok(pedidoService.filtrarPedidos(filtro));
    }

    // 3. Crear nuevo pedido usando DTO
    @PostMapping("/crear")
    public ResponseEntity<PedidoResponseDTO> crearPedido(@RequestBody CrearPedidoRequestDTO crearPedidoDTO) {
        return ResponseEntity.ok(pedidoService.crearPedido(crearPedidoDTO));
    }

    // 4. Actualizar estado de pedido usando DTO
    @PutMapping("/{pedidoId}/estado")
    public ResponseEntity<PedidoResponseDTO> actualizarEstado(
            @PathVariable Integer pedidoId,
            @RequestBody ActualizarEstadoRequestDTO actualizarEstadoDTO) {
        return ResponseEntity.ok(pedidoService.actualizarEstadoPedido(pedidoId, actualizarEstadoDTO.getNuevoEstado()));
    }

    // 5. Obtener pedido por ID
    @GetMapping("/{pedidoId}")
    public ResponseEntity<PedidoResponseDTO> obtenerPedidoPorId(@PathVariable Integer pedidoId) {
        return ResponseEntity.ok(pedidoService.obtenerPedidoPorId(pedidoId));
    }

    // 6. Obtener pedidos del usuario actual (autenticado)
    @GetMapping("/mis-pedidos")
    public ResponseEntity<List<PedidoResponseDTO>> getMisPedidos(@RequestHeader("Authorization") String authHeader) {
        // Extraer ID del usuario del token JWT
        // Esta implementación depende de cómo manejes la autenticación
        // Por ahora, asumiremos que el usuario ya está autenticado y su ID está disponible
        // En la práctica, necesitarías un método para extraer el userId del token
        String token = authHeader.substring(7); // Remover "Bearer "
        // Aquí deberías extraer el userId del token
        // Por ahora, usaremos un endpoint alternativo que requiera el userId
        return ResponseEntity.status(501).build(); // Not Implemented
    }

    // 7. Cancelar pedido (solo si está pendiente)
    @PutMapping("/{pedidoId}/cancelar")
    public ResponseEntity<PedidoResponseDTO> cancelarPedido(@PathVariable Integer pedidoId) {
        return ResponseEntity.ok(pedidoService.actualizarEstadoPedido(pedidoId, "CANCELADO"));
    }
    
    // 8. Obtener estadísticas del dashboard (NUEVO)
    @GetMapping("/dashboard/estadisticas")
    public ResponseEntity<DashboardDTO> getEstadisticasDashboard() {
        return ResponseEntity.ok(pedidoService.obtenerDashboardActualizado());
    }
    
    // 9. Forzar actualización del dashboard (NUEVO)
    @PostMapping("/dashboard/actualizar")
    public ResponseEntity<Void> actualizarDashboard() {
        // Este endpoint puede usarse para limpiar cachés o forzar actualizaciones
        // Por ahora, solo devolvemos OK ya que el servicio actualiza en tiempo real
        return ResponseEntity.ok().build();
    }
}