package proyect.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyect.app.entity.productos;

public interface ProductoRepository extends JpaRepository<productos, Integer> {

}
