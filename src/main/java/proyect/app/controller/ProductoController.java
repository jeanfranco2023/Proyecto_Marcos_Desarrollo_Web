package proyect.app.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import proyect.app.dto.ProductoForm;
import proyect.app.entity.Categoria;
import proyect.app.entity.Colores;
import proyect.app.entity.Productos;
import proyect.app.entity.Talla;
import proyect.app.service.CategoriaService;
import proyect.app.service.ColoresService;
import proyect.app.service.ProductoService;
import proyect.app.service.TallaService;

@Controller
@RequestMapping("/productos")
public class ProductoController {
    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private TallaService tallaService;

    @Autowired
    private ColoresService coloresService;

    @GetMapping("/ropa")
    public String listarRopa(@RequestParam String categoria, Model model, HttpSession usuario) {
        model.addAttribute("productos", productoService.listar());
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("tallas", tallaService.listar());
        model.addAttribute("categoria", categoria);
        return "ropas";
    }

    @GetMapping("/register/principal")
    public String Productos() {
        return "principalProductos";
    }

    @GetMapping("/register/listar")
    @Transactional(readOnly = true)
    public String listarProductos(Model model) {
        List<Productos> listaProductos = productoService.listarProductosConRelaciones();

        model.addAttribute("productos", listaProductos);
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("tallas", tallaService.listar());
        model.addAttribute("colores", coloresService.listar());

        return "listaProductos";
    }

    @GetMapping("/register/agregar")
    @Transactional(readOnly = true)
    public String agregarProducto(@RequestParam(required = false) Integer id, Model model) {
        List<Productos> listaProductos = productoService.listarProductosConRelaciones();
        List<Categoria> listaCategorias = categoriaService.listar();
        List<Talla> listaTallas = tallaService.listar();
        List<Colores> listaColores = coloresService.listar();

        ProductoForm productoForm;

        if (id != null) {
            Productos producto = productoService.buscarPorId(id);
            productoForm = new ProductoForm();
            if (producto != null) {
                productoForm.setIdProducto(producto.getIdProducto());
                productoForm.setNombreProducto(producto.getNombreProducto());
                productoForm.setDescripcionProducto(producto.getDescripcionProducto());
                productoForm.setPrecioProducto(producto.getPrecioProducto());
                productoForm.setStockProducto(producto.getStockProducto());
                productoForm.setEstrellasProducto(producto.getEstrellasProducto());

                if (!producto.getCategorias().isEmpty()) {
                    productoForm.setCategoriasSeleccionadas(
                            Arrays.asList(producto.getCategorias().iterator().next().getIdCategoria()));
                }

                productoForm.setTallasSeleccionadas(
                        producto.getTallas().stream()
                                .map(t -> t.getId())
                                .collect(Collectors.toList()));

                productoForm.setColoresSeleccionados(
                        producto.getColores().stream()
                                .map(c -> c.getId())
                                .collect(Collectors.toList()));

                model.addAttribute("editando", true);
            }
        } else {
            productoForm = new ProductoForm();
        }

        model.addAttribute("producto", productoForm); // <- solo aquí una vez
        model.addAttribute("productos", listaProductos);
        model.addAttribute("categorias", listaCategorias);
        model.addAttribute("tallas", listaTallas);
        model.addAttribute("colores", listaColores);

        return "agregarProducto";
    }

    @PostMapping("/guardarProducto")
    public String guardarProducto(
            @ModelAttribute ProductoForm form,
            RedirectAttributes redirectAttributes) {

        try {
            Productos producto;

            if (form.getIdProducto() != null) {
                // Modo edición
                producto = productoService.buscarPorId(form.getIdProducto());
                if (producto == null) {
                    redirectAttributes.addFlashAttribute("error", "Producto no encontrado.");
                    return "redirect:/productos/agregar";
                }
            } else {
                // Modo nuevo
                producto = new Productos();
            }

            producto.setNombreProducto(form.getNombreProducto());
            producto.setDescripcionProducto(form.getDescripcionProducto());
            producto.setPrecioProducto(form.getPrecioProducto());
            producto.setStockProducto(form.getStockProducto());
            producto.setEstrellasProducto(form.getEstrellasProducto());

            if (form.getImagenProducto() != null && !form.getImagenProducto().isEmpty()) {
                producto.setImagenProducto(form.getImagenProducto().getOriginalFilename());
            }

            if (form.getCategoriasSeleccionadas() != null && !form.getCategoriasSeleccionadas().isEmpty()) {
                List<Categoria> categorias = form.getCategoriasSeleccionadas().stream()
                        .map(categoriaService::buscarPorId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                producto.setCategorias(new HashSet<>(categorias));
            }

            if (form.getTallasSeleccionadas() != null && !form.getTallasSeleccionadas().isEmpty()) {
                List<Talla> tallas = form.getTallasSeleccionadas().stream()
                        .map(tallaService::buscarPorId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                producto.setTallas(new HashSet<>(tallas));
            }

            if (form.getColoresSeleccionados() != null && !form.getColoresSeleccionados().isEmpty()) {
                List<Colores> colores = form.getColoresSeleccionados().stream()
                        .map(coloresService::buscarPorId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                producto.setColores(new HashSet<>(colores));
            }

            productoService.insertar(producto);

            String mensaje = (form.getIdProducto() != null)
                    ? "Producto actualizado correctamente"
                    : "Producto registrado correctamente";

            redirectAttributes.addFlashAttribute("success", mensaje);

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/productos/agregar";
        }

        return "redirect:/productos/register/listar";
    }

    @PostMapping("/eliminar")
    public String eliminarProducto(@RequestParam Integer idProducto, RedirectAttributes redirectAttributes) {
        try {
            Productos producto = productoService.buscarPorId(idProducto);
            if (producto != null) {
                productoService.eliminar(producto);
            } else {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
                return "redirect:/productos/listar";
            }
            redirectAttributes.addFlashAttribute("success", "Producto eliminado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/productos/listar";
    }

    @GetMapping("/register/registrar/categoria")
    public String registrarCategoria(Model model) {
        model.addAttribute("categorias", new Categoria());
        model.addAttribute("listaCategorias", categoriaService.listar());
        return "registroCategoria";
    }

    @PostMapping("/guardarCategoria")
    public String guardarCategoria(Categoria categoria) {
        if (categoriaService.buscarPorNombreCategoria(categoria.getNombreCategoria()) == null) {
            categoriaService.insertar(categoria);
        } else {
            return "redirect:/productos/registrar/categoria?error=Categoria ya existe";
        }
        return "redirect:/productos/registrar/categ oria";
    }

    @GetMapping("/producto/{idProducto}")
    public String verProducto(@PathVariable Integer idProducto, Model model) {
        Productos producto = productoService.buscarPorId(idProducto);
        model.addAttribute("productoElegido", producto);
        model.addAttribute("categorias", categoriaService.listar());
        return "producto";
    }

    @ModelAttribute("categoriaHombres")
    public List<Categoria> getCategoriasHombres() {
        List<Categoria> categorias = categoriaService.listar();
        List<Categoria> categoriasHombres = categorias.stream()
                .filter(categoria -> "Masculino".equals(categoria.getSexoCategoria()) ||
                        "Unisex".equals(categoria.getSexoCategoria()))
                .collect(Collectors.toList());
        return categoriasHombres;
    }

    @ModelAttribute("categoriaMujeres")
    public List<Categoria> getCategoriasMujeres() {
        List<Categoria> categorias = categoriaService.listar();
        List<Categoria> categoriasMujeres = categorias.stream()
                .filter(categoria -> "Femenino".equals(categoria.getSexoCategoria()) ||
                        "Unisex".equals(categoria.getSexoCategoria()))
                .collect(Collectors.toList());
        return categoriasMujeres;
    }

    @ModelAttribute("usuario")
    public String getUsuarioIniciado(HttpSession session) {
        Object usuario = session.getAttribute("usuarioIniciado");
        return usuario != null ? usuario.toString() : null;
    }

    @ModelAttribute("esAdmin")
    public boolean esAdmin(HttpSession session) {
        Boolean admin = (Boolean) session.getAttribute("administrador");
        return admin != null && admin;
    }
}
