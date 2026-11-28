
package cl.duoc.perfulandia.usuario.model;

import java.time.LocalDate;
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
import lombok.NoArgsConstructor;
import lombok.Setter;

// =============================================
// PERFULANDIA - MICROSERVICIO DE USUARIOS
// ENTIDAD USUARIO
// =============================================

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    // IDENTIFICADOR INTERNO
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RUT DEL USUARIO
    @Column(nullable = false, unique = true, length = 12)
    private String rut;

    // NOMBRE COMPLETO
    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 120)
    private String apellidos;

    private LocalDate fechaNacimiento;

    // CORREO ELECTRÓNICO
    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    // CONTRASEÑA ALMACENADA COMO HASH
    // NUNCA GUARDAR LA CONTRASEÑA ORIGINAL
    @Column(nullable = false, length = 255)
    private String passwordHash;

    // TELÉFONO OPCIONAL
    @Column(length = 20)
    private String telefono;

    // DIRECCIÓN OPCIONAL
    @Column(length = 255)
    private String direccion;

    // REGIÓN Y COMUNA
    @Column(length = 100)
    private String region;

    @Column(length = 100)
    private String comuna;

    // ROL DEL USUARIO
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RolUsuario rol = RolUsuario.CLIENTE;

    // ESTADO DE LA CUENTA
    @Column(nullable = false)
    private boolean activo = true;

    // FECHA DE REGISTRO
    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    // ROLES DISPONIBLES
    public enum RolUsuario {
        CLIENTE,
        VENDEDOR,
        ADMINISTRADOR
    }
}
