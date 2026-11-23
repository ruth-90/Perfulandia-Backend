
package cl.duoc.perfulandia.usuario.dto;

import cl.duoc.perfulandia.usuario.model.Usuario.RolUsuario;


// RESPUESTA DEL INICIO DE SESION
//Devuelve los datos basicos del usuario y el token JWT
//El token permite autenticar solicitudes

public record LoginResponse(

        Long id,

        String nombre,

        String correo,

        RolUsuario rol,

        String token,

        String mensaje

) {
}
