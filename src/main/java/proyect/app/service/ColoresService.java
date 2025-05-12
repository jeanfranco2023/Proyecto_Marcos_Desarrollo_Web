package proyect.app.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proyect.app.entity.Colores;
import proyect.app.repository.ColorRepository;

@Service
public class ColoresService implements ServicesInterface<Colores> {

    @Autowired
    private ColorRepository colorRepository;

    @Override
    public List<Colores> listar() {
        return colorRepository.findAll();
    }

    @Override
    public Colores buscarPorId(Integer id) {
        return colorRepository.findById(id).orElse(null);
    }

    @Override
    public void insertar(Colores objeto) {
        colorRepository.save(objeto);
    }

    @Override
    public void eliminar(Colores objeto) {
        colorRepository.delete(objeto);
    }

    public Colores buscarPorNombre(String nombre) {
        return colorRepository.findByNombre(nombre);
    }

    public Colores buscarPorCodigoHex(String codigoHex) {
        return colorRepository.findByCodigoHex(codigoHex);
    }

    public Set<Colores> obtenerColoresPorIds(List<Integer> coloresIds) {
        Set<Colores> colores = colorRepository.findAllById(coloresIds).stream().collect(Collectors.toSet());
        return colores;
    }

    public List<Colores> obtenerColoresPorProducto(Integer productoId) {
        return colorRepository.findColoresByProductoId(productoId);
    }
    
}
