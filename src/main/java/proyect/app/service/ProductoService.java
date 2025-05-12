package proyect.app.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import proyect.app.entity.Productos;
import proyect.app.repository.ProductoRepository;

@Service
public class ProductoService implements ServicesInterface<Productos> {
    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<Productos> listar() {
        return productoRepository.findAll();
    }

    @Override
    public Productos buscarPorId(Integer id) {
        return productoRepository.findById(id).orElse(null);
    }

    @Override
    public void insertar(Productos producto) {
        productoRepository.save(producto);
    }

    @Override
    public void eliminar(Productos producto) {
        productoRepository.delete(producto);
    }

    public Object buscarPorNombreProducto(String nombreProducto) {
        return productoRepository.findByNombreProducto(nombreProducto);
    }

    @Transactional(readOnly = true)
    public List<Productos> listarProductosConRelaciones() {
        return productoRepository.findAllWithRelationships();
    }

    public Productos save(Productos producto) {
        return productoRepository.save(producto);
    }

}
