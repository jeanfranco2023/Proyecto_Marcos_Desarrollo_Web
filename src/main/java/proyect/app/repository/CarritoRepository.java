package proyect.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import proyect.app.entity.Carrito;

public interface CarritoRepository extends JpaRepository<Carrito,Integer>{
    
}
