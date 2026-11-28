
package cl.duoc.perfulandia.producto.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import jakarta.persistence.EntityManager;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import cl.duoc.perfulandia.producto.exception.ResourceNotFoundException;
import cl.duoc.perfulandia.producto.dto.ProductoCreateRequest;
import cl.duoc.perfulandia.producto.dto.ProductoUpdateRequest;
import cl.duoc.perfulandia.producto.dto.ProductoResponse;
import cl.duoc.perfulandia.producto.model.Producto;
import cl.duoc.perfulandia.producto.model.DescuentoPedido;
import cl.duoc.perfulandia.producto.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

// PERFULANDIA - SERVICIO DE PRODUCTOS
// Contiene la logica de negocio del microservicio PRODUCTO.

@Service
@RequiredArgsConstructor
public class ProductoService {

    // Repositorio para acceder a la base de datos.
    private final ProductoRepository productoRepository;
    private final EntityManager entityManager;


    // 1. LISTAR TODOS LOS PRODUCTOS
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarProductos() {

        return productoRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }


    // 2. BUSCAR PRODUCTO POR ID
    @Transactional(readOnly = true)
    public ProductoResponse buscarPorId(Long id) {

        Producto producto = buscarEntidadPorId(id);

        return convertirAResponse(producto);
    }


    // 3. CREAR PRODUCTO
    @Transactional
    public ProductoResponse crearProducto(
            ProductoCreateRequest request) {

        Producto producto = new Producto();

        producto.setNombre(request.nombre().trim());
        producto.setCategoria(request.categoria());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        producto.setImagen(request.imagen());
        producto.setDescripcion(request.descripcion());

        Producto productoGuardado =
                productoRepository.save(producto);

        return convertirAResponse(productoGuardado);
    }


    // 4. ACTUALIZAR PRODUCTO
    @Transactional
    public ProductoResponse actualizarProducto(
            Long id,
            ProductoUpdateRequest request) {

        Producto productoExistente = buscarEntidadPorId(id);

        productoExistente.setNombre(request.nombre().trim());
        productoExistente.setCategoria(request.categoria());
        productoExistente.setPrecio(request.precio());
        productoExistente.setStock(request.stock());
        productoExistente.setImagen(request.imagen());
        productoExistente.setDescripcion(request.descripcion());

        Producto productoActualizado =
                productoRepository.save(productoExistente);

        return convertirAResponse(productoActualizado);
    }


    // 5. ELIMINAR PRODUCTO
    @Transactional
    public void eliminarProducto(Long id) {

        Producto productoExistente = buscarEntidadPorId(id);

        productoRepository.delete(productoExistente);
    }


    // 6. DESCONTAR STOCK DE UN PRODUCTO
    // Este metodo permite descontar inventario.
    @Transactional
    public ProductoResponse descontarStock(
            Long id,
            Integer cantidad) {

        if (cantidad == null || cantidad <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor que cero");
        }
        aplicarDescuento(id, cantidad);
        return convertirAResponse(buscarEntidadPorId(id));
    }

    @Transactional
    public void descontarStockPedido(UUID operacion, Map<Long, Integer> cantidades) {
        if (cantidades == null || cantidades.isEmpty() || cantidades.entrySet().stream()
                .anyMatch(linea -> linea.getKey() == null || linea.getKey() <= 0
                        || linea.getValue() == null || linea.getValue() <= 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Productos o cantidades inválidos");
        }
        var lineas = cantidades.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList();
        String contenido = lineas.toString();
        DescuentoPedido registrado = entityManager.find(DescuentoPedido.class, operacion);
        if (registrado != null) {
            if (!registrado.getContenido().equals(contenido)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El descuento registrado tiene otros productos o cantidades");
            }
            return;
        }
        // La clave única evita aplicar dos veces la operación incluso ante solicitudes simultáneas.
        entityManager.persist(new DescuentoPedido(operacion, contenido));
        entityManager.flush();
        lineas.forEach(linea -> aplicarDescuento(linea.getKey(), linea.getValue()));
    }

    private void aplicarDescuento(Long id, Integer cantidad) {
        if (productoRepository.descontarStock(id, cantidad) == 0) {
            buscarEntidadPorId(id);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock insuficiente para el producto " + id);
        }
    }


    // 7. BUSCAR ENTIDAD POR ID
    // Si el producto no existe, lanzar excepcion.
    private Producto buscarEntidadPorId(Long id) {

        return productoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No se encontro el producto con ID: " + id
                        )
                );
    }


    // 8. CONVERTIR ENTIDAD A DTO
    // Convierte un Producto en ProductoResponse.
    private ProductoResponse convertirAResponse(
            Producto producto) {

        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getCategoria(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getImagen(),
                producto.getDescripcion()
        );
    }
}
