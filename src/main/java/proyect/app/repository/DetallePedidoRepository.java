package proyect.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import proyect.app.entity.DetallePedido;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {
    
}
