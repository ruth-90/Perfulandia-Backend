
package cl.duoc.perfulandia.producto.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Indica que esta clase representa una entidad en la base de datos.
@Entity

// Nombre de la tabla que se creará en H2.
@Table(name = "productos")

// Lombok genera automáticamente getters y setters.
@Getter
@Setter

// Constructor sin parámetros, requerido por JPA.
@NoArgsConstructor

// Constructor con todos los atributos.
@AllArgsConstructor
public class Producto {

    // Identificador único del producto.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El nombre es obligatorio.
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Column(nullable = false, length = 150)
    private String nombre;

    // Categoría: Hombre, Mujer o Unisex.
    @NotBlank(message = "La categoría es obligatoria")
    @Column(nullable = false, length = 30)
    private String categoria;

    // Precio obligatorio y mayor que cero.
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    // El stock no puede ser negativo.
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer stock;

    // Guarda la ruta de imagen o su representación Base64.
    @NotBlank(message = "La imagen es obligatoria")
    @Lob
    @Column(nullable = false)
    private String imagen;

    // Descripción opcional del perfume.
    @Column(length = 2000)
    private String descripcion;
}
