
package cl.duoc.perfulandia.pedido_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {

    // Identificador único del detalle
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Pedido al que pertenece este detalle
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    // ID del perfume en producto-service
    @Column(nullable = false)
    private Long productoId;

    // Nombre del perfume al momento de comprar
    @Column(nullable = false)
    private String nombre;

    // Cantidad de unidades compradas
    @Column(nullable = false)
    private Integer cantidad;

    // Precio de una unidad en pesos chilenos
    @Column(nullable = false)
    private Long precioUnitario;

    // Precio total de este detalle
    @Column(nullable = false)
    private Long subtotal;

    // Constructor vacío requerido por JPA
    public DetallePedido() {
    }

    // GETTERS Y SETTERS

    public Long getId() {
        return id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Long getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Long precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Long getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Long subtotal) {
        this.subtotal = subtotal;
    }
}
