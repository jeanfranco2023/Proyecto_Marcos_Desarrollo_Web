package proyect.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import proyect.app.entity.productos;
import proyect.app.repository.ProductoRepository;

public class ProductoService implements ServicesInterface<productos> {
    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<productos> listar() {
        return productoRepository.findAll();
    }

    @Override
    public productos buscarPorId(Integer id) {
        return productoRepository.findById(id).orElse(null);
    }

    @Override
    public void insertar(productos producto) {
        productoRepository.save(producto);
    }

    @Override
    public void eliminar(productos producto) {
        productoRepository.delete(producto);
    }

}
