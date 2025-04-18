package proyect.app.service;

import java.util.List;

public interface ServicesInterface<T> {
    public List<T> listar();

    public T buscarPorId(Integer id);

    public void insertar(T objeto);

    public void eliminar(T objeto); 

    
}
