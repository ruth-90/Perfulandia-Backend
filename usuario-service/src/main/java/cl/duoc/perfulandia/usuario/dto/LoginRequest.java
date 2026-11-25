
package cl.duoc.perfulandia.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// =============================================
// PERFULANDIA - MICROSERVICIO DE USUARIOS
// DATOS PARA INICIAR SESION
// =============================================

public record LoginRequest(

        @NotBlank(message = "El correo es obligatorio.")
        @Email(message = "Ingresa un correo válido.")
        String correo,

        @NotBlank(message = "La contraseña es obligatoria.")
        String password

) {
}
