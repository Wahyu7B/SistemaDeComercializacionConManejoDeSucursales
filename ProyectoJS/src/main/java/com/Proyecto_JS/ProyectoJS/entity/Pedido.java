package com.Proyecto_JS.ProyectoJS.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id")
    private Carrito carrito;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(nullable = false, length = 3)
    private String moneda;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_entrega", nullable = false)
    private TipoEntrega tipoEntrega;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id")
    private Sucursal sucursalRecojo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "direccion_envio_id")
    private DireccionEnvio direccionEnvio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado;

    @Column(name = "nota_cliente", length = 255)
    private String notaCliente;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private Set<PedidoDetalle> detalles;

    public enum TipoEntrega {
        RECOJO_TIENDA, DELIVERY
    }

    public enum EstadoPedido {
        CREADO, PAGO_EN_REVISION, PAGADO, ANULADO
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Carrito getCarrito() {
        return carrito;
    }

    public void setCarrito(Carrito carrito) {
        this.carrito = carrito;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public TipoEntrega getTipoEntrega() {
        return tipoEntrega;
    }

    public void setTipoEntrega(TipoEntrega tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

    public Sucursal getSucursalRecojo() {
        return sucursalRecojo;
    }

    public void setSucursalRecojo(Sucursal sucursalRecojo) {
        this.sucursalRecojo = sucursalRecojo;
    }

    public DireccionEnvio getDireccionEnvio() {
        return direccionEnvio;
    }

    public void setDireccionEnvio(DireccionEnvio direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public String getNotaCliente() {
        return notaCliente;
    }

    public void setNotaCliente(String notaCliente) {
        this.notaCliente = notaCliente;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<PedidoDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(Set<PedidoDetalle> detalles) {
        this.detalles = detalles;
    }
    
}