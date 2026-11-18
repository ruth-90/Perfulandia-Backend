
package cl.duoc.perfulandia.producto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.perfulandia.producto.model.Producto;

// Repositorio encargado del acceso a los productos en H2.
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Comprueba si ya existe un perfume con ese nombre.
    // Evita duplicar productos durante la carga inicial.
    boolean existsByNombre(String nombre);

}
