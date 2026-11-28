
package cl.duoc.perfulandia.usuario.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import cl.duoc.perfulandia.usuario.model.Usuario.RolUsuario;

// =============================================
// PERFULANDIA - MICROSERVICIO DE USUARIOS
// DTO DE RESPUESTA
// NO EXPONE CONTRASEÑAS NI HASHES
// =============================================

public record UsuarioResponse(

        // IDENTIFICADOR DEL USUARIO
        Long id,

        // DATOS PERSONALES
        String rut,
        String nombre,
        String correo,

        // DATOS OPCIONALES
        String telefono,
        String direccion,
        String region,
        String comuna,

        // ROL Y ESTADO DE LA CUENTA
        RolUsuario rol,
        boolean activo,

        // FECHA DE REGISTRO
        LocalDateTime fechaRegistro,
        String apellidos,
        LocalDate fechaNacimiento

) {
}
