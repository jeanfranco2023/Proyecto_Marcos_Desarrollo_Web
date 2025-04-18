package proyect.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyect.app.entity.usuarios;
import proyect.app.repository.UsuarioRepository;

@Service
public class UsuarioService implements ServicesInterface<usuarios> {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<usuarios> listar() {
        return usuarioRepository.findAll();
    }

    @Override
    public usuarios buscarPorId(Integer id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    public void insertar(usuarios objeto) {
        usuarioRepository.save(objeto);
    }

    @Override
    public void eliminar(usuarios usuario) {
        usuarioRepository.delete(usuario);
    }

    public usuarios buscarPorcorreoUsuario(String email) {
        return usuarioRepository.findByCorreoUsuario(email).orElse(null);
    }

}
