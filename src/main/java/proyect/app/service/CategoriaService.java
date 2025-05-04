package proyect.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proyect.app.entity.Categoria;
import proyect.app.repository.CategoriaRepository;

@Service
public class CategoriaService implements ServicesInterface<Categoria> {
    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }

    @Override
    public Categoria buscarPorId(Integer id) {
        return categoriaRepository.findById(id).orElse(null);
    }

    @Override
    public void insertar(Categoria objeto) {
        categoriaRepository.save(objeto);
    }

    @Override
    public void eliminar(Categoria objeto) {
       categoriaRepository.delete(objeto);
    }

    public Object buscarPorNombreCategoria(String nombreCategoria) {
        return categoriaRepository.findByNombreCategoria(nombreCategoria);
    }

    

}
