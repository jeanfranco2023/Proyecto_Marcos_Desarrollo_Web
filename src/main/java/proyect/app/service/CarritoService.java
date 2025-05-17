package proyect.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proyect.app.entity.Carrito;
import proyect.app.repository.CarritoRepository;

@Service
public class CarritoService implements ServicesInterface<Carrito> {

    @Autowired
    private CarritoRepository carritoRepository;

    @Override
    public List<Carrito> listar() {
        return carritoRepository.findAll();
    }

    @Override
    public Carrito buscarPorId(Integer id) {
        return carritoRepository.findById(id).orElse(null);
    }

    @Override
    public void insertar(Carrito objeto) {
        carritoRepository.save(objeto);
    }

    @Override
    public void eliminar(Carrito objeto) {
        carritoRepository.delete(objeto);
    }


}
