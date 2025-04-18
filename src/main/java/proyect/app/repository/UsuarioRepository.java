package proyect.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import proyect.app.entity.usuarios;

public interface UsuarioRepository extends JpaRepository<usuarios, Integer> {
    Optional<usuarios> findByCorreoUsuario(String correoUsuario);
}
