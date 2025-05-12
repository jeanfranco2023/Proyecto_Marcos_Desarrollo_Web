package proyect.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import proyect.app.entity.Talla;

public interface TallaRepository extends JpaRepository<Talla, Integer> {

    @Query(value = "SELECT t.* FROM tallas t " +
            "JOIN producto_talla pt ON t.id = pt.talla_id " +
            "WHERE pt.producto_id = :productoId", nativeQuery = true)
    List<Talla> findTallasByProductoId(@Param("productoId") Integer productoId);
}
