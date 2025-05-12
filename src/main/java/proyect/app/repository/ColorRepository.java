package proyect.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import proyect.app.entity.Colores;

public interface ColorRepository extends JpaRepository<Colores, Integer> {
    Colores findByNombre(String nombre);

    Colores findByCodigoHex(String codigoHex);

    @Query(value = "SELECT c.* FROM colores c " +
            "JOIN producto_color pc ON c.id_color = pc.color_id " +
            "WHERE pc.producto_id = :productoId", nativeQuery = true)
    List<Colores> findColoresByProductoId(@Param("productoId") Integer productoId);

}
