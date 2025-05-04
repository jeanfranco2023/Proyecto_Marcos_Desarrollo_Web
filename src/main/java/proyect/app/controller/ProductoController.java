package proyect.app.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import proyect.app.entity.Categoria;
import proyect.app.entity.Productos;
import proyect.app.service.CategoriaService;
import proyect.app.service.ProductoService;

@Controller
@RequestMapping("/productos")
public class ProductoController {
    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;
    
    @GetMapping("/agregar")
    public String agregarProducto(Model model) {
        model.addAttribute("producto", new Productos());
        model.addAttribute("productos", productoService.listar());
        model.addAttribute("categorias", categoriaService.listar());
        return "agregarProducto";
    }

    @PostMapping("/guardarProducto")
    public String guardarProducto(Productos producto) {
        if(productoService.buscarPorNombreProducto(producto.getNombreProducto()) == null) {
            productoService.insertar(producto);
        } else {
           return "redirect:/productos/agregar?error=Producto ya existe";
        }
        
        return "redirect:/productos/agregar";
    }

    @GetMapping("/registrar/categoria")
    public String registrarCategoria(Model model) {
        model.addAttribute("categorias",new Categoria());
        model.addAttribute("listaCategorias", categoriaService.listar());
        return "registroCategoria";
    }

    @PostMapping("/guardarCategoria")
    public String guardarCategoria(Categoria categoria) {
        if(categoriaService.buscarPorNombreCategoria(categoria.getNombreCategoria()) == null) {
            categoriaService.insertar(categoria);
        } else {
            return "redirect:/productos/registrar/categoria?error=Categoria ya existe";
        }
        return "redirect:/productos/registrar/categoria";
    }
}
