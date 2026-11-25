
package cl.duoc.perfulandia.pedido_service.controller;

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
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    // Conectar el controlador con la capa Service
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // Obtener el ID del usuario validado por el backend
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

    // Verificar permisos de administrador
    private void verificarAdmin(HttpServletRequest request) {

        obtenerUsuarioId(request);

        Object rol = request.getAttribute("rol");

        if (!"ADMIN".equals(rol)) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Se requieren permisos de administrador"
            );
        }
    }

    // POST: crear un pedido
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

    // GET: consultar mis pedidos
    @GetMapping("/mis-pedidos")
    public List<PedidoResponse> misPedidos(
            HttpServletRequest request) {

        Long usuarioId = obtenerUsuarioId(request);

        return pedidoService.misPedidos(usuarioId);
    }

    // GET: consultar un pedido específico
    @GetMapping("/{id}")
    public PedidoResponse buscar(
            @PathVariable Long id,
            HttpServletRequest request) {

        Long usuarioId = obtenerUsuarioId(request);

        return pedidoService.buscarMiPedido(id, usuarioId);
    }

    // GET: listar todos los pedidos (ADMIN)
    @GetMapping
    public List<PedidoResponse> listarTodos(
            HttpServletRequest request) {

        verificarAdmin(request);

        return pedidoService.listarTodos();
    }
}
    