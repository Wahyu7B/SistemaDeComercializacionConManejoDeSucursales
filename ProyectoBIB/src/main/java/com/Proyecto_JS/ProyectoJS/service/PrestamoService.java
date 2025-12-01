package com.Proyecto_JS.ProyectoJS.service;

import com.Proyecto_JS.ProyectoJS.dto.DevolucionDTO;
import com.Proyecto_JS.ProyectoJS.dto.PrestamoDTO;
import com.Proyecto_JS.ProyectoJS.entity.*;
import com.Proyecto_JS.ProyectoJS.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PrestamoService {

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private PrestamoDetalleRepository prestamoDetalleRepository;

    @Autowired
    private ConfiguracionPrestamoRepository configuracionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SucursalRepository sucursalRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    /**
     * Crear un nuevo préstamo
     */
    @Transactional
    public Prestamo crearPrestamo(PrestamoDTO dto, Long adminId) {
        // 1. Validar que el usuario existe
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Validar que la sucursal existe
        Sucursal sucursal = sucursalRepository.findById(dto.getSucursalId())
            .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));

        // 3. Obtener configuración de préstamos
        ConfiguracionPrestamo config = configuracionRepository.findById(1L)
            .orElseThrow(() -> new RuntimeException("Configuración de préstamos no encontrada"));

        // 4. Validar que los préstamos estén habilitados
        if (!config.getPermitePrestamos()) {
            throw new RuntimeException("Los préstamos están deshabilitados temporalmente");
        }

        // 5. Validar límite de préstamos activos por usuario
        Long prestamosActivos = prestamoRepository.countPrestamosActivosByUsuario(usuario.getId());
        if (prestamosActivos >= config.getMaxPrestamosPorUsuario()) {
            throw new RuntimeException("El usuario ha alcanzado el límite de préstamos activos (" 
                + config.getMaxPrestamosPorUsuario() + ")");
        }

        // 6. Validar disponibilidad de libros en inventario
        for (PrestamoDTO.DetalleLibroDTO libroDTO : dto.getLibros()) {
            Libro libro = libroRepository.findById(libroDTO.getLibroId())
                .orElseThrow(() -> new RuntimeException("Libro no encontrado: " + libroDTO.getLibroId()));

            Inventario inventario = inventarioRepository.findBySucursalIdAndLibroId(
                sucursal.getId(), libro.getId())
                .orElseThrow(() -> new RuntimeException("El libro '" + libro.getTitulo() 
                    + "' no está disponible en esta sucursal"));

            if (inventario.getStockPrestamo() < libroDTO.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para préstamo del libro: " + libro.getTitulo() 
                    + ". Disponible: " + inventario.getStockPrestamo());
            }
        }

        // 7. Crear el préstamo
        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario);
        prestamo.setSucursal(sucursal);
        prestamo.setFechaPrestamo(LocalDateTime.now());
        
        int diasPrestamo = dto.getDiasPrestamo() != null ? dto.getDiasPrestamo() : config.getDiasPrestamoDefault();
        prestamo.setDiasPrestamo(diasPrestamo);
        prestamo.setFechaDevolucionEsperada(LocalDateTime.now().plusDays(diasPrestamo));
        
        prestamo.setEstado(Prestamo.EstadoPrestamo.ACTIVO);
        prestamo.setMulta(BigDecimal.ZERO);
        prestamo.setObservaciones(dto.getObservaciones());

        // Asignar admin que registra el préstamo
        if (adminId != null) {
            usuarioRepository.findById(adminId).ifPresent(prestamo::setAdminPrestamo);
        }

        // Guardar préstamo
        prestamo = prestamoRepository.save(prestamo);

        // 8. Crear detalles y reducir inventario
        for (PrestamoDTO.DetalleLibroDTO libroDTO : dto.getLibros()) {
            Libro libro = libroRepository.findById(libroDTO.getLibroId()).get();
            
            // Crear detalle
            PrestamoDetalle detalle = new PrestamoDetalle();
            detalle.setPrestamo(prestamo);
            detalle.setLibro(libro);
            detalle.setCantidad(libroDTO.getCantidad());
            prestamoDetalleRepository.save(detalle);

            // Reducir stock de préstamo
            Inventario inventario = inventarioRepository.findBySucursalIdAndLibroId(
                sucursal.getId(), libro.getId()).get();
            inventario.setStockPrestamo(inventario.getStockPrestamo() - libroDTO.getCantidad());
            inventarioRepository.save(inventario);
        }

        return prestamo;
    }

    /**
     * Procesar devolución de un préstamo
     */
    @Transactional
    public Prestamo procesarDevolucion(DevolucionDTO dto) {
        // 1. Buscar el préstamo
        Prestamo prestamo = prestamoRepository.findById(dto.getPrestamoId())
            .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        // 2. Validar que el préstamo está activo
        if (prestamo.getEstado() != Prestamo.EstadoPrestamo.ACTIVO) {
            throw new RuntimeException("El préstamo no está activo");
        }

        // 3. Calcular multa si hay retraso
        LocalDateTime ahora = LocalDateTime.now();
        if (ahora.isAfter(prestamo.getFechaDevolucionEsperada())) {
            ConfiguracionPrestamo config = configuracionRepository.findById(1L).get();
            long diasRetraso = ChronoUnit.DAYS.between(prestamo.getFechaDevolucionEsperada(), ahora);
            BigDecimal multa = config.getMultaPorDia().multiply(BigDecimal.valueOf(diasRetraso));
            prestamo.setMulta(multa);
        }

        // 4. Actualizar estado del préstamo
        prestamo.setFechaDevolucionReal(ahora);
        prestamo.setEstado(Prestamo.EstadoPrestamo.DEVUELTO);
        
        if (dto.getObservaciones() != null) {
            prestamo.setObservaciones(prestamo.getObservaciones() + "\n[Devolución] " + dto.getObservaciones());
        }

        // Asignar admin que procesa la devolución
        if (dto.getAdminDevolucionId() != null) {
            usuarioRepository.findById(dto.getAdminDevolucionId())
                .ifPresent(prestamo::setAdminDevolucion);
        }

        // 5. Restaurar inventario
        List<PrestamoDetalle> detalles = prestamoDetalleRepository.findByPrestamoId(prestamo.getId());
        for (PrestamoDetalle detalle : detalles) {
            Inventario inventario = inventarioRepository.findBySucursalIdAndLibroId(
                prestamo.getSucursal().getId(), detalle.getLibro().getId()).get();
            inventario.setStockPrestamo(inventario.getStockPrestamo() + detalle.getCantidad());
            inventarioRepository.save(inventario);
        }

        return prestamoRepository.save(prestamo);
    }

    /**
     * Marcar préstamos vencidos automáticamente
     */
    @Transactional
    public void marcarPrestamosVencidos() {
        List<Prestamo> prestamosVencidos = prestamoRepository.findPrestamosVencidos(LocalDateTime.now());
        
        ConfiguracionPrestamo config = configuracionRepository.findById(1L).get();
        
        for (Prestamo prestamo : prestamosVencidos) {
            // Calcular multa
            long diasRetraso = ChronoUnit.DAYS.between(prestamo.getFechaDevolucionEsperada(), LocalDateTime.now());
            BigDecimal multa = config.getMultaPorDia().multiply(BigDecimal.valueOf(diasRetraso));
            
            prestamo.setEstado(Prestamo.EstadoPrestamo.VENCIDO);
            prestamo.setMulta(multa);
            prestamoRepository.save(prestamo);
        }
    }

    /**
     * Listar todos los préstamos
     */
    public List<Prestamo> listarTodos() {
        return prestamoRepository.findAll();
    }

    /**
     * Listar préstamos por usuario
     */
    public List<Prestamo> listarPorUsuario(Long usuarioId) {
        return prestamoRepository.findByUsuarioIdOrderByFechaPrestamoDesc(usuarioId);
    }

    /**
     * Listar préstamos activos de un usuario
     */
    public List<Prestamo> listarActivosPorUsuario(Long usuarioId) {
        return prestamoRepository.findPrestamosActivosByUsuario(usuarioId);
    }

    /**
     * Listar préstamos por estado
     */
    public List<Prestamo> listarPorEstado(Prestamo.EstadoPrestamo estado) {
        return prestamoRepository.findByEstadoOrderByFechaPrestamoDesc(estado);
    }

    /**
     * Obtener un préstamo por ID
     */
    public Prestamo obtenerPorId(Long id) {
        return prestamoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));
    }

    /**
     * Obtener detalles de un préstamo
     */
    public List<PrestamoDetalle> obtenerDetalles(Long prestamoId) {
        return prestamoDetalleRepository.findByPrestamoId(prestamoId);
    }
}
