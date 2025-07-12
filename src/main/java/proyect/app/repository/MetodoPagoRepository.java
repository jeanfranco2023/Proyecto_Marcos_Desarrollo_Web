package proyect.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import proyect.app.entity.MetodoPago;

public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Integer> {
    
}
