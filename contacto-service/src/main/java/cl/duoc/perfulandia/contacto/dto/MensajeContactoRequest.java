
package cl.duoc.perfulandia.contacto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// =============================================
// PERFULANDIA - MICROSERVICIO DE CONTACTO
// DTO PARA RECIBIR MENSAJES DESDE REACT
// =============================================

public record MensajeContactoRequest(

    // NOMBRE OBLIGATORIO
    @NotBlank(message = "El nombre es obligatorio.")
    @Size(
        max = 120,
        message = "El nombre no puede superar los 120 caracteres."
    )
    String nombre,

    // CORREO OPCIONAL, PERO CON FORMATO VÁLIDO
    @Email(message = "El correo electrónico no es válido.")
    @Size(
        max = 150,
        message = "El correo no puede superar los 150 caracteres."
    )
    String correo,

    // MENSAJE OBLIGATORIO
    @NotBlank(message = "El comentario es obligatorio.")
    @Size(
        max = 3000,
        message = "El comentario no puede superar los 3000 caracteres."
    )
    String comentario

) {
}
