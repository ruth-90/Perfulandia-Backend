
package cl.duoc.perfulandia.usuario.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// =============================================
// PERFULANDIA - MICROSERVICIO DE USUARIOS
// DTO PARA REGISTRAR NUEVOS USUARIOS
// =============================================

public record RegistroUsuarioRequest(

        // RUT OBLIGATORIO
        // LA VALIDACION DEL DIGITO VERIFICADOR
        // SE REALIZARA EN LA CAPA DE SERVICIO
        @NotBlank(message = "El RUT es obligatorio.")
        @Size(max = 12, message = "El RUT no puede superar los 12 caracteres.")
        String rut,

        // NOMBRE COMPLETO
        @NotBlank(message = "El nombre es obligatorio.")
        @Size(max = 120, message = "El nombre no puede superar los 120 caracteres.")
        String nombre,

        // CORREO ELECTRONICO
        @NotBlank(message = "El correo es obligatorio.")
        @Email(message = "Ingresa un correo válido.")
        @Size(max = 150, message = "El correo no puede superar los 150 caracteres.")
        String correo,

        // CONTRASEÑA EN TEXTO PLANO
        // SOLO SE RECIBE PARA GENERAR EL HASH
        // NUNCA SE GUARDA DIRECTAMENTE
        @Size(
                min = 8,
                max = 72,
                message = "La contraseña debe tener entre 8 y 72 caracteres."
        )
        String password,

        // DATOS OPCIONALES
        @Size(max = 20)
        String telefono,

        @Size(max = 255)
        String direccion,

        @Size(max = 100)
        String region,

        @Size(max = 100)
        String comuna,

        @Size(max = 120)
        String apellidos,

        LocalDate fechaNacimiento

) {
}
