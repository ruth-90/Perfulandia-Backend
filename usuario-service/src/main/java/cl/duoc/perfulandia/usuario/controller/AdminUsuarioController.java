
package cl.duoc.perfulandia.usuario.controller;

import cl.duoc.perfulandia.usuario.dto.UsuarioResponse;
import cl.duoc.perfulandia.usuario.model.Usuario;
import cl.duoc.perfulandia.usuario.repository.UsuarioRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
 * CONTROLADOR ADMINISTRATIVO
 * Permite consultar los usuarios registrados.
 * No devuelve contrasenas ni hashes BCrypt.
 */

@RestController
@RequestMapping("/api/v1/admin/usuarios")
public class AdminUsuarioController {

    private final UsuarioRepository usuarioRepository;

    public AdminUsuarioController(
            UsuarioRepository usuarioRepository
    ) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {

        List<UsuarioResponse> usuarios = usuarioRepository
                .findAll()
                .stream()
                .map(this::convertirARespuesta)
                .toList();

        return ResponseEntity.ok(usuarios);
    }

    // CONVERTIR ENTIDAD A RESPUESTA SEGURA
    private UsuarioResponse convertirARespuesta(Usuario usuario) {

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getRut(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getTelefono(),
                usuario.getDireccion(),
                usuario.getRegion(),
                usuario.getComuna(),
                usuario.getRol(),
                usuario.isActivo(),
                usuario.getFechaRegistro()
        );
    }
}
