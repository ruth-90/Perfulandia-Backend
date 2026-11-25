
package cl.duoc.perfulandia.contacto.dto;

import java.time.LocalDateTime;

import cl.duoc.perfulandia.contacto.model.MensajeContacto.EstadoMensaje;

// =============================================
// PERFULANDIA - MICROSERVICIO DE CONTACTO
// DTO PARA DEVOLVER MENSAJES AL FRONTEND
// =============================================

public record MensajeContactoResponse(

    // IDENTIFICADOR DEL MENSAJE
    Long id,

    // DATOS DE QUIEN ENVIÓ EL MENSAJE
    String nombre,
    String correo,
    String comentario,

    // FECHA EN QUE SE RECIBIÓ
    LocalDateTime fechaCreacion,

    // ESTADO: PENDIENTE O RESPONDIDO
    EstadoMensaje estado

) {
}