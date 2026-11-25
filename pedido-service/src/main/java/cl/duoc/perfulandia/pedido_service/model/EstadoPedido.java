
package cl.duoc.perfulandia.pedido_service.model;

// Estados posibles de un pedido en Perfulandia
public enum EstadoPedido {

    // Pedido registrado, pendiente de confirmación
    PENDIENTE,

    // Pedido confirmado correctamente
    CONFIRMADO,

    // Pedido cancelado
    CANCELADO
}
    