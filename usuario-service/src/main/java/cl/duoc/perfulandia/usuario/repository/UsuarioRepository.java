
package cl.duoc.perfulandia.usuario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.perfulandia.usuario.model.Usuario;

// =============================================
// PERFULANDIA - MICROSERVICIO DE USUARIOS
// REPOSITORIO DE USUARIOS
// =============================================

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // BUSCAR UN USUARIO POR SU RUT
    Optional<Usuario> findByRut(String rut);

    // BUSCAR UN USUARIO POR SU CORREO
    Optional<Usuario> findByCorreoIgnoreCase(String correo);

    // COMPROBAR SI UN RUT YA ESTÁ REGISTRADO
    boolean existsByRut(String rut);

    // COMPROBAR SI UN CORREO YA ESTÁ REGISTRADO
    boolean existsByCorreoIgnoreCase(String correo);
}
