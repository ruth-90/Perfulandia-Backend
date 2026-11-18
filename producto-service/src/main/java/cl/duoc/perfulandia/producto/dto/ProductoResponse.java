
package cl.duoc.perfulandia.producto.dto;

import java.math.BigDecimal;

// DTO que devuelve los datos de un producto
// desde Spring Boot hacia React.
public record ProductoResponse(

    // Identificador único generado por la base de datos.
    Long id,

    // Nombre del perfume.
    String nombre,

    // Categoría: Hombre, Mujer o Unisex.
    String categoria,

    // Precio del perfume.
    BigDecimal precio,

    // Cantidad disponible.
    Integer stock,

    // Ruta o contenido de la imagen.
    String imagen,

    // Descripción del perfume.
    String descripcion

) {}
