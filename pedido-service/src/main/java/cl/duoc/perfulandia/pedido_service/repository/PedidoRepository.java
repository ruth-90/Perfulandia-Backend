
package cl.duoc.perfulandia.pedido_service.repository;

import cl.duoc.perfulandia.pedido_service.model.Pedido;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Buscar todos los pedidos de un cliente
    // Ordenados desde el más reciente
    List<Pedido> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    // Buscar un pedido verificando que pertenezca al cliente
    Optional<Pedido> findByIdAndUsuarioId(Long id, Long usuarioId);

}
