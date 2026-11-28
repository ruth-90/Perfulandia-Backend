
package cl.duoc.perfulandia.pedido_service.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import cl.duoc.perfulandia.pedido_service.dto.CrearPedidoRequest;
import cl.duoc.perfulandia.pedido_service.dto.PedidoResponse;
import cl.duoc.perfulandia.pedido_service.service.PedidoService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    // Conectar el controlador con el servicio
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // Obtener el ID del usuario autenticado
    private Long obtenerUsuarioId(HttpServletRequest request) {

        Object valor = request.getAttribute("usuarioId");

        if (!(valor instanceof Long usuarioId) || usuarioId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Debes iniciar sesion"
            );
        }

        return usuarioId;
    }

    // POST - Crear un pedido
    @PostMapping
    public ResponseEntity<PedidoResponse> crear(
            @Valid @RequestBody CrearPedidoRequest datos,
            HttpServletRequest request) {

        Long usuarioId = obtenerUsuarioId(request);

        PedidoResponse pedido = pedidoService.crearPedido(
                datos,
                usuarioId
        );

        return ResponseEntity
                .created(URI.create("/api/v1/pedidos/" + pedido.id()))
                .body(pedido);
    }

    // GET - Consultar mis pedidos
    @GetMapping("/mis-pedidos")
    public List<PedidoResponse> misPedidos(
            HttpServletRequest request) {

        Long usuarioId = obtenerUsuarioId(request);

        return pedidoService.misPedidos(usuarioId);
    }

    // GET - Consultar un pedido por ID
    @GetMapping("/{id}")
    public PedidoResponse buscar(
            @PathVariable Long id,
            HttpServletRequest request) {

        Long usuarioId = obtenerUsuarioId(request);

        String rol = (String) request.getAttribute("rol");
        return "ADMINISTRADOR".equals(rol) || "VENDEDOR".equals(rol)
                ? pedidoService.buscarPedido(id)
                : pedidoService.buscarMiPedido(id, usuarioId);
    }

    @GetMapping
    public List<PedidoResponse> listarTodos() {
        return pedidoService.listarTodos();
    }

    @PutMapping("/{id}/confirmacion")
    public PedidoResponse confirmar(@PathVariable Long id, @RequestHeader("Authorization") String authorization) {
        return pedidoService.confirmarPedido(id, authorization);
    }
}
