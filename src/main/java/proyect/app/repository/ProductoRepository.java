package proyect.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyect.app.entity.Productos;

public interface ProductoRepository extends JpaRepository<Productos, Integer> {

    Object findByNombreProducto(String nombreProducto);

}
