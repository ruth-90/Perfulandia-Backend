
package cl.duoc.perfulandia.pedido_service.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    // Identificador único del pedido
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identificador del cliente que realiza la compra
    @Column(nullable = false)
    private Long usuarioId;

    // Fecha y hora de creación del pedido
    @Column(nullable = false)
    private OffsetDateTime fechaCreacion;

    // Estado del pedido
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    // Método de pago seleccionado
    @Column(nullable = false)
    private String metodoPago;

    // Valores monetarios en pesos chilenos
    @Column(nullable = false)
    private Long costoEnvio;

    @Column(nullable = false)
    private Long total;

    // Datos de entrega del cliente
    @Column(nullable = false)
    private String nombreEntrega;

    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private String comuna;

    @Column(nullable = false)
    private String direccion;
    // Un pedido puede contener varios productos
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles = new ArrayList<>();

    // Constructor requerido por JPA
    public Pedido() {
    }

    // Establecer la fecha al guardar un pedido nuevo
    @PrePersist
    public void antesDeGuardar() {
        if (fechaCreacion == null) {
            fechaCreacion = OffsetDateTime.now(ZoneOffset.UTC);
        }
    }

    // GETTERS Y SETTERS

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public OffsetDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Long getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(Long costoEnvio) {
        this.costoEnvio = costoEnvio;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public String getNombreEntrega() {
        return nombreEntrega;
    }

    public void setNombreEntrega(String nombreEntrega) {
        this.nombreEntrega = nombreEntrega;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getComuna() {
        return comuna;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    // Obtener los detalles del pedido
public List<DetallePedido> getDetalles() {
    return detalles;
}

// Agregar un producto al pedido
public void agregarDetalle(DetallePedido detalle) {
    detalles.add(detalle);
    detalle.setPedido(this);
}
}
