
package cl.duoc.perfulandia.usuario.controller;

import java.net.URI;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import cl.duoc.perfulandia.usuario.dto.RegistroUsuarioRequest;
import cl.duoc.perfulandia.usuario.dto.UsuarioResponse;
import cl.duoc.perfulandia.usuario.service.UsuarioService;
import cl.duoc.perfulandia.usuario.service.UsuarioService.UsuarioDuplicadoException;

import jakarta.validation.Valid;

// =============================================
// PERFULANDIA - MICROSERVICIO DE USUARIOS
// CONTROLADOR REST PARA REGISTRAR CLIENTES
// =============================================

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    // =========================================
    // POST - REGISTRAR UN CLIENTE
    // =========================================

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrarUsuario(
            @Valid @RequestBody RegistroUsuarioRequest request
    ) {

        UsuarioResponse registrado =
                service.registrarUsuario(request);

        URI ubicacion = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/usuarios/{id}")
                .buildAndExpand(registrado.id())
                .toUri();

        return ResponseEntity
                .created(ubicacion)
                .body(registrado);
    }

    // =========================================
    // ERROR: RUT O CORREO DUPLICADO
    // =========================================

    @ExceptionHandler(UsuarioDuplicadoException.class)
    public ResponseEntity<Map<String, String>> manejarDuplicado(
            UsuarioDuplicadoException error
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("mensaje", error.getMessage()));
    }

    // =========================================
    // ERROR: RUT INVÁLIDO
    // =========================================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> manejarDatosInvalidos(
            IllegalArgumentException error
    ) {
        return ResponseEntity
                .badRequest()
                .body(Map.of("mensaje", error.getMessage()));
    }
}
