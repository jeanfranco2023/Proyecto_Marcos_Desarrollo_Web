package proyect.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import proyect.app.entity.Productos;

public interface ProductoRepository extends JpaRepository<Productos, Integer> {

    Object findByNombreProducto(String nombreProducto);

    @EntityGraph(attributePaths = { "colores", "tallas", "categorias" })
    @Query("SELECT p FROM Productos p")
    List<Productos> findAllWithRelationships();
}
