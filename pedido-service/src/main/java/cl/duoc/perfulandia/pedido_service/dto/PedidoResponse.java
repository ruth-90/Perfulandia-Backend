
package cl.duoc.perfulandia.pedido_service.dto;

import cl.duoc.perfulandia.pedido_service.model.Pedido;

import cl.duoc.perfulandia.pedido_service.model.EstadoPedido;

import java.util.List;

// DTO que devuelve los datos del pedido a React
public record PedidoResponse(

    // Identificador del pedido
    Long id,

    // Identificador del cliente
    Long usuarioId,

    // Estado actual del pedido
    EstadoPedido estado,

    // Productos incluidos en la compra
    List<ItemResponse> items,

    // Costo del despacho en pesos chilenos
    Long costoEnvio,

    // Total de la compra
    Long total

) {

    // DTO que representa cada producto comprado
    public record ItemResponse(

        Long productoId,

        String nombre,

        Integer cantidad,

        Long precioUnitario,

        Long subtotal

    ) {
    }

    // Convierte una entidad Pedido en un PedidoResponse
    public static PedidoResponse desdeEntidad(Pedido pedido) {

        // Convertimos los detalles a objetos de respuesta
        List<ItemResponse> items = pedido.getDetalles()
            .stream()
            .map(detalle -> new ItemResponse(
                detalle.getProductoId(),
                detalle.getNombre(),
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                detalle.getSubtotal()
            ))
            .toList();

        // Construimos la respuesta que recibirá React
        return new PedidoResponse(
            pedido.getId(),
            pedido.getUsuarioId(),
            pedido.getEstado(),
            items,
            pedido.getCostoEnvio(),
            pedido.getTotal()
        );
    }
}
