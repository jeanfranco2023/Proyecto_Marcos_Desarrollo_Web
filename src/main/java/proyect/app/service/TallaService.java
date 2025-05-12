package proyect.app.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proyect.app.entity.Talla;
import proyect.app.repository.TallaRepository;

@Service
public class TallaService implements ServicesInterface<Talla> {

    @Autowired
    private TallaRepository tallaRepository;

    @Override
    public List<Talla> listar() {
        return tallaRepository.findAll();
    }

    @Override
    public Talla buscarPorId(Integer id) {
        return tallaRepository.findById(id).orElse(null);
    }

    @Override
    public void insertar(Talla objeto) {
        tallaRepository.save(objeto);
    }

    @Override
    public void eliminar(Talla objeto) {
        tallaRepository.delete(objeto);
    }

    public Set<Talla> obtenerTallasPorIds(List<Integer> tallasIds) {
        Set<Talla> tallas = tallaRepository.findAllById(tallasIds).stream().collect(Collectors.toSet());
        return tallas;
    }
    
    public List<Talla> obtenerTallasPorProductoId(Integer productoId) {
    return tallaRepository.findTallasByProductoId(productoId);
}
    
}
