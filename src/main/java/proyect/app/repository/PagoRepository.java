package proyect.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import proyect.app.entity.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
}