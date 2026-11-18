
package cl.duoc.perfulandia.producto.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

// DTO que recibe los datos enviados desde React
// cuando el administrador actualiza un perfume.
public record ProductoUpdateRequest(

    // Nuevo nombre del perfume.
    @NotBlank(message = "El nombre del producto es obligatorio")
    String nombre,

    // Solo se aceptan las categorías de nuestra tienda.
    @NotBlank(message = "La categoría es obligatoria")
    @Pattern(
        regexp = "Hombre|Mujer|Unisex",
        message = "La categoría debe ser Hombre, Mujer o Unisex"
    )
    String categoria,

    // El precio debe ser mayor que cero.
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(
        value = "0.01",
        message = "El precio debe ser mayor que cero"
    )
    BigDecimal precio,

    // El stock debe ser cero o mayor.
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    Integer stock,

    // Imagen del perfume.
    @NotBlank(message = "La imagen es obligatoria")
    String imagen,

    // Descripción opcional.
    String descripcion

) {}
