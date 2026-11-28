
package cl.duoc.perfulandia.producto.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.RequiredArgsConstructor;

import cl.duoc.perfulandia.producto.dto.ProductoCreateRequest;
import cl.duoc.perfulandia.producto.dto.ProductoUpdateRequest;
import cl.duoc.perfulandia.producto.dto.ProductoResponse;
import cl.duoc.perfulandia.producto.service.ProductoService;

// PERFULANDIA - CONTROLADOR DE PRODUCTOS
@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    // 1. LISTAR TODOS LOS PRODUCTOS
    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listarProductos() {

        List<ProductoResponse> productos =
                productoService.listarProductos();

        return ResponseEntity.ok(productos);
    }

    // 2. BUSCAR PRODUCTO POR ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> buscarPorId(
            @PathVariable Long id) {

        ProductoResponse producto =
                productoService.buscarPorId(id);

        return ResponseEntity.ok(producto);
    }

    // 3. CREAR PRODUCTO
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<ProductoResponse> crearProducto(
            @Valid @RequestBody ProductoCreateRequest request) {

        ProductoResponse productoCreado =
                productoService.crearProducto(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productoCreado);
    }

    // 4. ACTUALIZAR PRODUCTO
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoUpdateRequest request) {

        ProductoResponse productoActualizado =
                productoService.actualizarProducto(id, request);

        return ResponseEntity.ok(productoActualizado);
    }

    // 5. ELIMINAR PRODUCTO
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(
            @PathVariable Long id) {

        productoService.eliminarProducto(id);

        return ResponseEntity.noContent().build();
    }

    // 6. DESCONTAR STOCK DE UN PRODUCTO
    // Debe restringirse a una operación autorizada de inventario.
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{id}/stock/descontar")
    public ResponseEntity<ProductoResponse> descontarStock(
            @PathVariable Long id,
            @Valid @RequestBody DescontarStockRequest request) {

        ProductoResponse actualizado =
                productoService.descontarStock(
                        id,
                        request.cantidad()
                );

        return ResponseEntity.ok(actualizado);
    }

    // DATOS NECESARIOS PARA DESCONTAR STOCK
    public record DescontarStockRequest(
            @NotNull(message = "La cantidad es obligatoria.")
            @Positive(message = "La cantidad debe ser mayor que cero.")
            Integer cantidad
    ) {
    }
}
