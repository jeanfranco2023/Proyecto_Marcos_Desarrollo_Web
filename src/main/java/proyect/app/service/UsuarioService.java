package proyect.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyect.app.entity.Usuarios;
import proyect.app.repository.UsuarioRepository;

@Service
public class UsuarioService implements ServicesInterface<Usuarios> {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<Usuarios> listar() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuarios buscarPorId(Integer id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    public void insertar(Usuarios objeto) {
        usuarioRepository.save(objeto);
    }

    @Override
    public void eliminar(Usuarios usuario) {
        usuarioRepository.delete(usuario);
    }

    public Usuarios buscarPorcorreoUsuario(String email) {
        return usuarioRepository.findByCorreoUsuario(email).orElse(null);
    }

}
