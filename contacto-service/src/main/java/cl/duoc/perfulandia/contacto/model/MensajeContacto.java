
package cl.duoc.perfulandia.contacto.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

// =============================================
// PERFULANDIA - MICROSERVICIO DE CONTACTO
// ENTIDAD MENSAJE CONTACTO
// =============================================

@Entity
@Table(name = "mensajes_contacto")
@Getter
@Setter
@NoArgsConstructor
public class MensajeContacto {

    // IDENTIFICADOR ÚNICO DEL MENSAJE
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // NOMBRE DE LA PERSONA QUE ENVÍA EL MENSAJE
    @Column(nullable = false, length = 120)
    private String nombre;

    // CORREO ELECTRÓNICO DEL REMITENTE
    @Column(length = 150)
    private String correo;

    // CONTENIDO DEL MENSAJE
    @Column(nullable = false, length = 3000)
    private String comentario;

    // FECHA Y HORA DE RECEPCIÓN
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    // ESTADO DEL MENSAJE
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoMensaje estado = EstadoMensaje.PENDIENTE;

    // ESTADOS POSIBLES
    public enum EstadoMensaje {
        PENDIENTE,
        RESPONDIDO
    }
}
