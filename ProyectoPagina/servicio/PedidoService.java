package com.ProyectoPaginaWeb.ProyectoPagina.servicio;

import com.ProyectoPaginaWeb.ProyectoPagina.dto.reporte.*;
import com.ProyectoPaginaWeb.ProyectoPagina.dto.request.*;
import com.ProyectoPaginaWeb.ProyectoPagina.dto.response.*;
import com.ProyectoPaginaWeb.ProyectoPagina.modelo.*;
import com.ProyectoPaginaWeb.ProyectoPagina.repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private StockService stockService;

    // ============================================================
    // MÉTODOS DE CONVERSIÓN ENTIDAD <-> DTO
    // ============================================================

    private PedidoResponseDTO convertirAPedidoDTO(Pedido pedido) {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setId(pedido.getId());
        dto.setUsuarioId(pedido.getUsuario().getIdUsuario());
        dto.setNombreUsuario(pedido.getUsuario().getNombreCompleto());
        dto.setDniUsuario(pedido.getUsuario().getDni());
        dto.setFechaPedido(pedido.getFechaPedido());
        dto.setEstado(pedido.getEstado());
        dto.setTotal(pedido.getTotal());
        dto.setDireccionEnvio(pedido.getDireccionEnvio());
        dto.setMetodoPago(pedido.getMetodoPago());
        dto.setFechaActualizacion(pedido.getFechaActualizacion());

        // Convertir detalles
        List<DetallePedidoResponseDTO> detallesDTO = pedido.getDetalles().stream()
                .map(this::convertirADetallePedidoDTO)
                .collect(Collectors.toList());
        dto.setDetalles(detallesDTO);

        return dto;
    }

    private DetallePedidoResponseDTO convertirADetallePedidoDTO(DetallePedido detalle) {
        DetallePedidoResponseDTO dto = new DetallePedidoResponseDTO();
        dto.setId(detalle.getId());
        dto.setProductoId(detalle.getProducto().getId());
        dto.setNombreProducto(detalle.getProducto().getModelo());
        dto.setNumeroSerieProducto(detalle.getProducto().getNumeroSerie());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setTallaSeleccionada(detalle.getTallaSeleccionada());
        dto.setSubtotal(detalle.getSubtotal());
        return dto;
    }

    private PedidoResumenDTO convertirAPedidoResumenDTO(Pedido pedido) {
        PedidoResumenDTO dto = new PedidoResumenDTO();
        dto.setId(pedido.getId());
        dto.setClienteNombre(pedido.getUsuario().getNombreCompleto());
        dto.setFechaPedido(pedido.getFechaPedido());
        dto.setTotal(pedido.getTotal());
        dto.setEstado(pedido.getEstado());
        return dto;
    }

    // ============================================================
    // HISTORIAL DE PEDIDOS POR CLIENTE (con DTOs)
    // ============================================================

    public List<PedidoResponseDTO> obtenerHistorialPorCliente(Integer clienteId) {
        // Usar consulta personalizada en lugar de método derivado
        List<Pedido> pedidos = pedidoRepository.findByUsuario_IdUsuario(clienteId);
        // Cargar detalles para cada pedido
        pedidos.forEach(pedido -> pedido.getDetalles().size()); // Esto fuerza la carga de detalles
        return pedidos.stream()
                .map(this::convertirAPedidoDTO)
                .collect(Collectors.toList());
    }

    // ============================================================
    // GENERAR REPORTES DE VENTAS (con DTOs)
    // ============================================================

    public ReporteVentasDTO generarReporteVentas(String periodo) {
        ReporteVentasDTO reporte = new ReporteVentasDTO();

        switch (periodo.toUpperCase()) {
            case "DIARIO":
                reporte.setTotalVentas(pedidoRepository.findVentasDiarias());
                reporte.setPeriodo("Diario");
                break;

            case "SEMANAL":
                reporte.setTotalVentas(pedidoRepository.findVentasSemanales());
                reporte.setPeriodo("Semanal");
                break;

            case "MENSUAL":
                reporte.setTotalVentas(pedidoRepository.findVentasMensuales());
                reporte.setPeriodo("Mensual");
                break;

            default:
                throw new IllegalArgumentException("Periodo no válido. Use: DIARIO, SEMANAL o MENSUAL");
        }

        reporte.setTotalPedidos(pedidoRepository.count());
        reporte.setPedidosPorEstado(pedidoRepository.countPedidosByEstado());

        return reporte;
    }

    // ============================================================
    // GENERAR REPORTE DE STOCK (con DTOs)
    // ============================================================

    public List<ProductoStockDTO> generarReporteStock() {
        List<Producto> productos = productoRepository.findAllWithCategoria();

        return productos.stream().map(producto -> {
            ProductoStockDTO dto = new ProductoStockDTO();
            dto.setId(producto.getId());
            dto.setNumeroSerie(producto.getNumeroSerie());
            dto.setModelo(producto.getModelo());
            dto.setCategoria(producto.getCategoria().getName());
            dto.setPrecio(producto.getPrecio());
            dto.setStockTotal(producto.getStockTotal());
            dto.setTallasDisponibles(producto.getTallasDisponibles());

            // Detalle por talla
            List<Map<String, Object>> detalleTallas = new ArrayList<>();
            if (producto.getTallas() != null) {
                for (TallaProducto talla : producto.getTallas()) {
                    Map<String, Object> detalle = new HashMap<>();
                    detalle.put("talla", talla.getTalla());
                    detalle.put("stock", talla.getStock());
                    detalleTallas.add(detalle);
                }
            }
            dto.setDetalleTallas(detalleTallas);

            return dto;
        }).collect(Collectors.toList());
    }

    // ============================================================
    // FILTRAR PEDIDOS (con DTOs) - CORREGIDO
    // ============================================================

    public List<PedidoResponseDTO> filtrarPedidos(PedidoFiltroRequestDTO filtro) {
        List<Pedido> pedidos;

        if (filtro.getClienteId() != null && filtro.getEstado() != null &&
                filtro.getFechaInicio() != null && filtro.getFechaFin() != null) {

            LocalDateTime inicio = filtro.getFechaInicio().atStartOfDay();
            LocalDateTime fin = filtro.getFechaFin().atTime(LocalTime.MAX);

            // Primero filtramos por usuario y fechas usando el método con @Query
            pedidos = pedidoRepository.findByUsuarioIdAndFechaPedidoBetween(
                    filtro.getClienteId(), inicio, fin)
                    .stream()
                    // Luego filtramos por estado
                    .filter(p -> p.getEstado().equals(filtro.getEstado()))
                    .collect(Collectors.toList());

        } else if (filtro.getClienteId() != null && filtro.getEstado() != null) {
            // Usamos el nuevo método con @Query que filtra por usuario y estado
            pedidos = pedidoRepository.findByUsuarioIdAndEstado(filtro.getClienteId(), filtro.getEstado());

        } else if (filtro.getClienteId() != null && filtro.getFechaInicio() != null &&
                filtro.getFechaFin() != null) {

            LocalDateTime inicio = filtro.getFechaInicio().atStartOfDay();
            LocalDateTime fin = filtro.getFechaFin().atTime(LocalTime.MAX);
            pedidos = pedidoRepository.findByUsuarioIdAndFechaPedidoBetween(
                    filtro.getClienteId(), inicio, fin);

        } else if (filtro.getEstado() != null && filtro.getFechaInicio() != null &&
                filtro.getFechaFin() != null) {

            LocalDateTime inicio = filtro.getFechaInicio().atStartOfDay();
            LocalDateTime fin = filtro.getFechaFin().atTime(LocalTime.MAX);
            // Primero filtramos por fechas
            pedidos = pedidoRepository.findByFechaPedidoBetween(inicio, fin)
                    .stream()
                    // Luego filtramos por estado
                    .filter(p -> p.getEstado().equals(filtro.getEstado()))
                    .collect(Collectors.toList());

        } else if (filtro.getClienteId() != null) {
            pedidos = pedidoRepository.findByUsuario_IdUsuario(filtro.getClienteId());

        } else if (filtro.getEstado() != null) {
            pedidos = pedidoRepository.findByEstado(filtro.getEstado());

        } else if (filtro.getFechaInicio() != null && filtro.getFechaFin() != null) {
            LocalDateTime inicio = filtro.getFechaInicio().atStartOfDay();
            LocalDateTime fin = filtro.getFechaFin().atTime(LocalTime.MAX);
            pedidos = pedidoRepository.findByFechaPedidoBetween(inicio, fin);

        } else {
            pedidos = pedidoRepository.findAll();
        }

        // Cargar detalles para cada pedido
        pedidos.forEach(pedido -> {
            if (pedido.getDetalles() != null) {
                pedido.getDetalles().size(); // Esto fuerza la carga de detalles
            }
        });

        return pedidos.stream()
                .map(this::convertirAPedidoDTO)
                .collect(Collectors.toList());
    }

    // ============================================================
    // DASHBOARD (con DTOs) - MODIFICADO PARA TIEMPO REAL
    // ============================================================

    public DashboardDTO obtenerEstadisticasDashboard() {
        DashboardDTO dashboard = new DashboardDTO();

        // Ventas del día - USANDO NUEVO MÉTODO QUE INCLUYE ESTADOS RELEVANTES
        dashboard.setVentasHoy(pedidoRepository.findVentasHoy());

        // Total de pedidos
        dashboard.setTotalPedidos(pedidoRepository.count());

        // Pedidos por estado
        dashboard.setPedidosPorEstado(pedidoRepository.countPedidosByEstado());

        // Productos con stock bajo
        List<Producto> productosBajoStock = productoRepository.findAllWithCategoria()
                .stream()
                .filter(p -> p.getStockTotal() <= 10)
                .collect(Collectors.toList());

        dashboard.setCantidadProductosStockBajo(productosBajoStock.size());

        // Convertir productos a map
        List<Map<String, Object>> productosBajoStockMap = productosBajoStock.stream()
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.getId());
                    map.put("modelo", p.getModelo());
                    map.put("stockTotal", p.getStockTotal());
                    map.put("precio", p.getPrecio());
                    map.put("categoria", p.getCategoria().getName());
                    return map;
                })
                .collect(Collectors.toList());

        dashboard.setProductosStockBajo(productosBajoStockMap);

        // Últimos pedidos del día - USANDO NUEVO MÉTODO
        List<Pedido> pedidosHoy = pedidoRepository.findPedidosHoy()
                .stream()
                .limit(10)
                .collect(Collectors.toList());

        List<PedidoResumenDTO> ultimosPedidosDTO = pedidosHoy.stream()
                .map(this::convertirAPedidoResumenDTO)
                .collect(Collectors.toList());

        dashboard.setUltimosPedidos(ultimosPedidosDTO);

        return dashboard;
    }

    // ============================================================
    // CREAR NUEVO PEDIDO (con DTOs) - MODIFICADO CRITICAMENTE
    // ============================================================

    @Transactional
    public PedidoResponseDTO crearPedido(CrearPedidoRequestDTO crearPedidoDTO) {
        
        System.out.println("=== INICIANDO CREACIÓN DE PEDIDO ===");
        System.out.println("Usuario ID: " + crearPedidoDTO.getUsuarioId());
        System.out.println("Número de items: " + crearPedidoDTO.getItems().size());
        
        Usuario usuario = usuarioRepository.findById(crearPedidoDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Crear pedido con estado COMPLETADO para que aparezca en ventas
        Pedido pedido = new Pedido(usuario, crearPedidoDTO.getDireccionEnvio(), 
                                  crearPedidoDTO.getMetodoPago());
        
        // ESTABLECER ESTADO Y FECHA CORRECTOS
        pedido.setEstado("COMPLETADO");
        pedido.setFechaPedido(LocalDateTime.now());
        
        for (ItemPedidoRequestDTO item : crearPedidoDTO.getItems()) {
            System.out.println("Procesando item - Producto ID: " + item.getProductoId() + 
                              ", Talla: " + item.getTalla() + 
                              ", Cantidad: " + item.getCantidad());
            
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            
            System.out.println("Stock total ANTES: " + producto.getStockTotal());
            
            // Verificar stock usando StockService
            if (!stockService.verificarStock(item.getProductoId(), item.getTalla(), item.getCantidad())) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getModelo());
            }
            
            // ACTUALIZAR STOCK USANDO STOCKSERVICE - ESTO ACTUALIZA TALLA Y STOCKTOTAL
            stockService.actualizarStockCompleto(
                item.getProductoId(), 
                item.getTalla(), 
                item.getCantidad()
            );
            
            // Recargar producto para ver cambios
            productoRepository.flush(); // Forzar sincronización con BD
            producto = productoRepository.findById(item.getProductoId()).orElse(null);
            
            if (producto != null) {
                System.out.println("Stock total DESPUÉS: " + producto.getStockTotal());
            }
            
            System.out.println("---");
            
            // Crear detalle
            DetallePedido detalle = new DetallePedido(producto, item.getCantidad(), item.getTalla());
            pedido.agregarDetalle(detalle);
        }
        
        // Calcular total
        pedido.calcularTotal();
        
        // Guardar pedido
        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        
        System.out.println("=== FINALIZANDO CREACIÓN DE PEDIDO ===");
        
        // Convertir a DTO
        return convertirAPedidoDTO(pedidoGuardado);
    }

    // ============================================================
    // MÉTODOS ADICIONALES (si necesitas)
    // ============================================================

    public PedidoResponseDTO obtenerPedidoPorId(Integer pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // Cargar detalles
        pedido.getDetalles().size();

        return convertirAPedidoDTO(pedido);
    }

    @Transactional
    public PedidoResponseDTO actualizarEstadoPedido(Integer pedidoId, String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstado(nuevoEstado);
        Pedido pedidoActualizado = pedidoRepository.save(pedido);

        return convertirAPedidoDTO(pedidoActualizado);
    }

    // ============================================================
    // NUEVO MÉTODO: OBTENER ESTADÍSTICAS ACTUALIZADAS DEL DASHBOARD
    // ============================================================

    public DashboardDTO obtenerDashboardActualizado() {
        DashboardDTO dashboard = new DashboardDTO();

        // Ventas del día - USANDO NUEVO MÉTODO QUE INCLUYE ESTADOS RELEVANTES
        dashboard.setVentasHoy(pedidoRepository.findVentasHoy());

        // Total de pedidos
        dashboard.setTotalPedidos(pedidoRepository.count());

        // Pedidos por estado
        dashboard.setPedidosPorEstado(pedidoRepository.countPedidosByEstado());

        // Productos con stock bajo
        List<Producto> productosBajoStock = productoRepository.findAllWithCategoria()
                .stream()
                .filter(p -> p.getStockTotal() <= 10)
                .collect(Collectors.toList());

        dashboard.setCantidadProductosStockBajo(productosBajoStock.size());

        // Convertir productos a map
        List<Map<String, Object>> productosBajoStockMap = productosBajoStock.stream()
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.getId());
                    map.put("modelo", p.getModelo());
                    map.put("stockTotal", p.getStockTotal());
                    map.put("precio", p.getPrecio());
                    map.put("categoria", p.getCategoria().getName());
                    return map;
                })
                .collect(Collectors.toList());

        dashboard.setProductosStockBajo(productosBajoStockMap);

        // Últimos pedidos del día
        List<Pedido> pedidosHoy = pedidoRepository.findPedidosHoy()
                .stream()
                .limit(10)
                .collect(Collectors.toList());

        List<PedidoResumenDTO> ultimosPedidosDTO = pedidosHoy.stream()
                .map(this::convertirAPedidoResumenDTO)
                .collect(Collectors.toList());

        dashboard.setUltimosPedidos(ultimosPedidosDTO);

        return dashboard;
    }
}