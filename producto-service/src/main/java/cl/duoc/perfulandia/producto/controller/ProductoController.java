
package cl.duoc.perfulandia.producto.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import cl.duoc.perfulandia.producto.dto.ProductoCreateRequest;
import cl.duoc.perfulandia.producto.dto.ProductoUpdateRequest;
import cl.duoc.perfulandia.producto.dto.ProductoResponse;
import cl.duoc.perfulandia.producto.service.ProductoService;


// Indica que esta clase recibe solicitudes HTTP
// y devuelve respuestas en formato JSON.
@RestController

// Define la ruta principal de los productos.
@RequestMapping("/api/v1/productos")

// Lombok genera el constructor para inyectar el servicio.
@RequiredArgsConstructor
public class ProductoController {

    // Servicio encargado de la lógica de negocio.
    private final ProductoService productoService;


    // 1. LISTAR TODOS LOS PRODUCTOS
    // GET http://localhost:8081/api/v1/productos
    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listarProductos() {

        List<ProductoResponse> productos =
                productoService.listarProductos();

        return ResponseEntity.ok(productos);
    }


    // 2. BUSCAR PRODUCTO POR ID
    // GET http://localhost:8081/api/v1/productos/1
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> buscarPorId(
            @PathVariable Long id) {

        ProductoResponse producto =
                productoService.buscarPorId(id);

        return ResponseEntity.ok(producto);
    }


    // 3. CREAR PRODUCTO
    // POST http://localhost:8081/api/v1/productos
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
    // PUT http://localhost:8081/api/v1/productos/1
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
    // DELETE http://localhost:8081/api/v1/productos/1
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(
            @PathVariable Long id) {

        productoService.eliminarProducto(id);

        return ResponseEntity.noContent().build();
    }
}
