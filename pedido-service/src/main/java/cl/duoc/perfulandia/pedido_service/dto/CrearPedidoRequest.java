
package cl.duoc.perfulandia.pedido_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

// DTO que recibe los datos de una compra desde React
public record CrearPedidoRequest(

    // Productos que el cliente quiere comprar
    @NotEmpty(message = "El pedido debe tener productos")
    List<@Valid ItemRequest> items,

    // Información de entrega del pedido
    @NotNull(message = "Los datos de entrega son obligatorios")
    @Valid
    EntregaRequest entrega,

    // Método de pago elegido
    @NotBlank(message = "El método de pago es obligatorio")
    @Size(max = 100)
    String metodoPago

) {

    // Datos de cada producto del carrito
    public record ItemRequest(

        @NotNull(message = "El ID del producto es obligatorio")
        @Positive(message = "El ID debe ser positivo")
        Long productoId,

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor que cero")
        Integer cantidad

    ) {
    }

    // Datos de despacho del cliente
    public record EntregaRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100)
        String nombre,

        @NotBlank(message = "El teléfono es obligatorio")
        String telefono,

        @NotBlank(message = "La región es obligatoria")
        String region,

        @NotBlank(message = "La comuna es obligatoria")
        String comuna,

        @NotBlank(message = "La dirección es obligatoria")
        @Size(max = 255)
        String direccion,

        @Email
        @Size(max = 150)
        String correo

    ) {
    }
}
