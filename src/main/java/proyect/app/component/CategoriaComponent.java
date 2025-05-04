package proyect.app.component;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import proyect.app.entity.Categoria;
import proyect.app.service.CategoriaService;

@Component
public class CategoriaComponent implements Converter<String, Categoria> {

    @Autowired
    private CategoriaService categoriaService;

    @Override
    public Categoria convert(String id) {
        return categoriaService.buscarPorId(Integer.parseInt(id));
    }
}
