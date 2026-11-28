
package cl.duoc.perfulandia.producto.repository;

import cl.duoc.perfulandia.producto.model.Producto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Evitar duplicados durante la carga inicial.
    boolean existsByNombre(String nombre);

    // Descontar stock de forma atómica.
    @Modifying
    @Query("""
        UPDATE Producto p
        SET p.stock = p.stock - :cantidad
        WHERE p.id = :productoId
          AND p.stock >= :cantidad
        """)
    int descontarStock(
            @Param("productoId") Long productoId,
            @Param("cantidad") Integer cantidad
    );
}
