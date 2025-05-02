package proyect.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import proyect.app.entity.Usuarios;

public interface UsuarioRepository extends JpaRepository<Usuarios, Integer> {
    Optional<Usuarios> findByCorreoUsuario(String correoUsuario);
}
