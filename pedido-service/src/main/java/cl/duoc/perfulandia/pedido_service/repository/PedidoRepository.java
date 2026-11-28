
package cl.duoc.perfulandia.pedido_service.repository;

import cl.duoc.perfulandia.pedido_service.model.Pedido;
import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Pedido p where p.id = :id")
    Optional<Pedido> buscarParaConfirmar(@Param("id") Long id);

    // Buscar todos los pedidos de un cliente
    // Ordenados desde el más reciente
    List<Pedido> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    // Buscar un pedido verificando que pertenezca al cliente
    Optional<Pedido> findByIdAndUsuarioId(Long id, Long usuarioId);

}
