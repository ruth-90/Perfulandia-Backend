
package cl.duoc.perfulandia.producto.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cl.duoc.perfulandia.producto.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

import cl.duoc.perfulandia.producto.dto.ProductoCreateRequest;
import cl.duoc.perfulandia.producto.dto.ProductoUpdateRequest;
import cl.duoc.perfulandia.producto.dto.ProductoResponse;
import cl.duoc.perfulandia.producto.model.Producto;
import cl.duoc.perfulandia.producto.repository.ProductoRepository;


// Indica que esta clase contiene la lógica de negocio.
@Service

// Lombok genera el constructor para inyectar el repositorio.
@RequiredArgsConstructor
public class ProductoService {

    // Permite acceder a la base de datos H2.
    private final ProductoRepository productoRepository;


    // 1. LISTAR TODOS LOS PRODUCTOS
    // Solo consulta información, no modifica la base de datos.
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarProductos() {

        return productoRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }


    // 2. BUSCAR PRODUCTO POR ID
    // Busca el producto y devuelve sus datos como DTO.
    @Transactional(readOnly = true)
    public ProductoResponse buscarPorId(Long id) {

        Producto producto = buscarEntidadPorId(id);

        return convertirAResponse(producto);
    }


    // 3. CREAR PRODUCTO
    // Recibe los datos desde React y registra un nuevo perfume.
    @Transactional
    public ProductoResponse crearProducto(ProductoCreateRequest request) {

        // Creamos una entidad Producto sin asignarle un ID.
        // La base de datos generará el ID automáticamente.
        Producto producto = new Producto();

        // Copiamos los datos recibidos en el DTO.
        producto.setNombre(request.nombre().trim());
        producto.setCategoria(request.categoria());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        producto.setImagen(request.imagen());
        producto.setDescripcion(request.descripcion());

        // Guardamos el perfume en la base de datos H2.
        Producto productoGuardado = productoRepository.save(producto);

        // Convertimos la entidad guardada en un DTO de respuesta.
        return convertirAResponse(productoGuardado);
    }


    // 4. ACTUALIZAR PRODUCTO
    // Busca un perfume existente y modifica sus datos.
    @Transactional
    public ProductoResponse actualizarProducto(
            Long id,
            ProductoUpdateRequest request) {

        // Primero comprobamos que el perfume exista.
        Producto productoExistente = buscarEntidadPorId(id);

        // Actualizamos sus atributos con los datos del DTO.
        productoExistente.setNombre(request.nombre().trim());
        productoExistente.setCategoria(request.categoria());
        productoExistente.setPrecio(request.precio());
        productoExistente.setStock(request.stock());
        productoExistente.setImagen(request.imagen());
        productoExistente.setDescripcion(request.descripcion());

        // Guardamos los cambios dentro de la transacción.
        Producto productoActualizado =
                productoRepository.save(productoExistente);

        // Devolvemos la información actualizada a React.
        return convertirAResponse(productoActualizado);
    }


    // 5. ELIMINAR PRODUCTO
    // Elimina un perfume después de comprobar su existencia.
    @Transactional
    public void eliminarProducto(Long id) {

        Producto productoExistente = buscarEntidadPorId(id);

        productoRepository.delete(productoExistente);
    }


    // 6. BUSCAR ENTIDAD POR ID
    // Método auxiliar para obtener la entidad desde H2.
    // Si no existe, lanza una excepción HTTP 404
    private Producto buscarEntidadPorId(Long id) {

        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el producto con ID: " + id
                ));
    }


    // 7. CONVERTIR ENTIDAD A DTO
    // Transforma un Producto de H2 en un ProductoResponse.
    private ProductoResponse convertirAResponse(Producto producto) {

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
