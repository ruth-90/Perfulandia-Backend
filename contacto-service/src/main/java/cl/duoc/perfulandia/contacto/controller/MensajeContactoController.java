
package cl.duoc.perfulandia.contacto.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import cl.duoc.perfulandia.contacto.dto.MensajeContactoRequest;
import cl.duoc.perfulandia.contacto.dto.MensajeContactoResponse;
import cl.duoc.perfulandia.contacto.service.MensajeContactoService;
import cl.duoc.perfulandia.contacto.service.MensajeContactoService.MensajeNoEncontradoException;

import jakarta.validation.Valid;

// =============================================
// PERFULANDIA - MICROSERVICIO DE CONTACTO
// CONTROLADOR REST DE MENSAJES
// =============================================

@RestController
@RequestMapping("/api/v1/contactos")
public class MensajeContactoController {

    private final MensajeContactoService service;

    // INYECTAR EL SERVICIO
    public MensajeContactoController(
            MensajeContactoService service
    ) {
        this.service = service;
    }

    // =========================================
    // POST - REGISTRAR MENSAJE DE CONTACTO
    // =========================================

    @PostMapping
    public ResponseEntity<MensajeContactoResponse> crearMensaje(
            @Valid @RequestBody MensajeContactoRequest request
    ) {

        MensajeContactoResponse creado =
                service.crearMensaje(request);

        URI ubicacion = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.id())
                .toUri();

        return ResponseEntity
                .created(ubicacion)
                .body(creado);
    }

    // =========================================
    // GET - LISTAR TODOS LOS MENSAJES
    // =========================================

    @GetMapping
    public ResponseEntity<List<MensajeContactoResponse>> listarMensajes() {

        return ResponseEntity.ok(
                service.listarMensajes()
        );
    }

    // =========================================
    // GET - CONSULTAR UN MENSAJE POR ID
    // =========================================

    @GetMapping("/{id}")
    public ResponseEntity<MensajeContactoResponse> obtenerMensaje(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                service.obtenerMensajePorId(id)
        );
    }

    // =========================================
    // PATCH - MARCAR MENSAJE COMO RESPONDIDO
    // =========================================

    @PatchMapping("/{id}/estado")
    public ResponseEntity<MensajeContactoResponse> marcarRespondido(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                service.marcarComoRespondido(id)
        );
    }

    // =========================================
    // DELETE - ELIMINAR UN MENSAJE
    // =========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMensaje(
            @PathVariable Long id
    ) {

        service.eliminarMensaje(id);

        return ResponseEntity.noContent().build();
    }

    // =========================================
    // MANEJO DE MENSAJES NO ENCONTRADOS
    // =========================================

    @ExceptionHandler(MensajeNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> manejarNoEncontrado(
            MensajeNoEncontradoException error
    ) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "mensaje",
                        error.getMessage()
                ));
    }
}
