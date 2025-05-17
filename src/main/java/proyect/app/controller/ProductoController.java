package proyect.app.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletResponse;
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

    @GetMapping("/agregar")
    @Transactional(readOnly = true)
    public String agregarProducto(Model model) {
        // Carga los productos con sus relaciones en una sola consulta
        List<Productos> listaProductos = productoService.listarProductosConRelaciones();

        // Si realmente necesitas las listas separadas (evalúa si es necesario)
        List<Categoria> listaCategorias = categoriaService.listar();
        List<Talla> listaTallas = tallaService.listar();
        List<Colores> listaColores = coloresService.listar();

        // Prepara los datos para la vista
        model.addAttribute("productos", listaProductos);
        model.addAttribute("categorias", listaCategorias);
        model.addAttribute("tallas", listaTallas);
        model.addAttribute("colores", listaColores);
        model.addAttribute("producto", new ProductoForm());

        return "agregarProducto";
    }

    @PostMapping("/guardarProducto")
    public String guardarProducto(
            @ModelAttribute ProductoForm form,
            RedirectAttributes redirectAttributes,
            HttpServletResponse response) {

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // Evita cache

        try {
            Productos producto = new Productos();
            producto.setNombreProducto(form.getNombreProducto());
            producto.setDescripcionProducto(form.getDescripcionProducto());
            producto.setPrecioProducto(form.getPrecioProducto());
            producto.setStockProducto(form.getStockProducto());
            producto.setEstrellasProducto(form.getEstrellasProducto()); // Inicializa estrellas en 0

            // Procesar imagen
            if (form.getImagenProducto() != null && !form.getImagenProducto().isEmpty()) {
                producto.setImagenProducto(form.getImagenProducto().getOriginalFilename());
            }

            // Procesar categorías
            if (form.getCategoriasSeleccionadas() != null && !form.getCategoriasSeleccionadas().isEmpty()) {
                List<Categoria> categorias = form.getCategoriasSeleccionadas().stream()
                        .map(categoriaService::buscarPorId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                producto.setCategorias(categorias.stream().collect(Collectors.toSet()));
            }

            if (form.getTallasSeleccionadas() != null && !form.getTallasSeleccionadas().isEmpty()) {
                List<Talla> tallas = form.getTallasSeleccionadas().stream()
                        .map(tallaService::buscarPorId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                producto.setTallas(tallas.stream().collect(Collectors.toSet()));
            }

            if (form.getColoresSeleccionados() != null && !form.getColoresSeleccionados().isEmpty()) {
                List<Colores> colores = form.getColoresSeleccionados().stream()
                        .map(coloresService::buscarPorId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                producto.setColores(colores.stream().collect(Collectors.toSet()));
            }

            productoService.insertar(producto);
            redirectAttributes.addFlashAttribute("success", "Producto registrado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/productos/agregar";
        }
        return "redirect:/productos/agregar";
    }

    @PostMapping("/eliminar")
    public String eliminarProducto(@RequestParam Integer idProducto, RedirectAttributes redirectAttributes) {
        try {
            Productos producto = productoService.buscarPorId(idProducto);
            if (producto != null) {
                productoService.eliminar(producto);
            } else {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
                return "redirect:/productos/agregar";
            }
            redirectAttributes.addFlashAttribute("success", "Producto eliminado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/productos/agregar";
    }

    @GetMapping("/obtener/{id}")
    @ResponseBody
    public Map<String, Object> obtenerProductoParaEditar(@PathVariable Integer id, ModelMap model) {
        Productos producto = productoService.buscarPorId(id);
        Map<String, Object> response = new HashMap<>();

        response.put("idProducto", producto.getIdProducto());
        response.put("nombreProducto", producto.getNombreProducto());
        response.put("descripcionProducto", producto.getDescripcionProducto());
        response.put("precioProducto", producto.getPrecioProducto());
        response.put("stockProducto", producto.getStockProducto());
        response.put("estrellasProducto", producto.getEstrellasProducto());

        // Obtener primera categoría (si existe)
        if (!producto.getCategorias().isEmpty()) {
            response.put("categoriaId", producto.getCategorias().iterator().next().getIdCategoria());
        }

        // Obtener IDs de tallas y colores
        response.put("tallas", producto.getTallas().stream()
                .map(Talla::getValor)
                .collect(Collectors.toList()));

        response.put("colores", producto.getColores().stream()
                .map(Colores::getNombre)
                .collect(Collectors.toList()));

        // Obtener IDs de categorías
        response.put("categorias", producto.getCategorias().stream()
                .map(Categoria::getIdCategoria)
                .collect(Collectors.toList()));
        // Obtener ID de la imagen (si existe)

        model.put("producto", response);
        System.out.println("idProducto: " + producto.getIdProducto());
        return response;
    }

    @PostMapping("/editar")
    public String editarProducto(@ModelAttribute("producto") ProductoForm productoForm,
            RedirectAttributes redirectAttributes) {
        try {
            // Obtener el producto
            Productos producto = productoService.buscarPorId(productoForm.getIdProducto());

            if (producto != null) {
                // Actualizar los campos del producto
                producto.setNombreProducto(productoForm.getNombreProducto());
                producto.setDescripcionProducto(productoForm.getDescripcionProducto());
                producto.setPrecioProducto(productoForm.getPrecioProducto());
                producto.setStockProducto(productoForm.getStockProducto());

                // Procesar tallas seleccionadas
                if (productoForm.getTallasSeleccionadas() != null && !productoForm.getTallasSeleccionadas().isEmpty()) {
                    List<Talla> tallas = productoForm.getTallasSeleccionadas().stream()
                            .map(tallaService::buscarPorId)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    producto.setTallas(new HashSet<>(tallas));
                }

                // Procesar colores seleccionados
                if (productoForm.getColoresSeleccionados() != null
                        && !productoForm.getColoresSeleccionados().isEmpty()) {
                    List<Colores> colores = productoForm.getColoresSeleccionados().stream()
                            .map(coloresService::buscarPorId)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    producto.setColores(new HashSet<>(colores));
                }

                // Guardar el producto actualizado
                productoService.save(producto);
                redirectAttributes.addFlashAttribute("success", "Producto actualizado correctamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/productos/agregar";
    }

    @GetMapping("/registrar/categoria")
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
