package proyect.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import proyect.app.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    Categoria findByNombreCategoria(String nombreCategoria);
}
