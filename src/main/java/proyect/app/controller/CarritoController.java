package proyect.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

import proyect.app.dto.CarritoDTO;
import proyect.app.entity.Categoria;
import proyect.app.entity.Productos;
import proyect.app.service.CategoriaService;
import proyect.app.service.ProductoService;

@Controller
@RequestMapping("/carrito")
@SessionAttributes("carrito")
public class CarritoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @ModelAttribute("carrito")
    public List<CarritoDTO> crearCarrito() {
        return new ArrayList<>();
    }

    @PostMapping("/agregar")
    public String agregarAlCarrito(
            @RequestParam("productoId") Integer productoId,
            @RequestParam("color") String color,
            @RequestParam("talla") String talla,
            @RequestParam("cantidad") int cantidad,
            @RequestParam("imagen") String imagen,
            @ModelAttribute("carrito") List<CarritoDTO> carrito) {

        Productos producto = productoService.buscarPorId(productoId);
        if (producto != null) {
            CarritoDTO item = new CarritoDTO();
            item.setProducto(producto);
            item.setColor(color);
            item.setTalla(talla);
            item.setCantidad(cantidad);
            item.setImagen(imagen);
            carrito.add(item);
        }
        System.out.println("Producto agregado: " + productoId);

        return "redirect:/carrito/ver";
    }

    @PostMapping("/eliminar")
    public String eliminarDelCarrito(@RequestParam Integer productoId,
            @RequestParam String color,
            @RequestParam String talla,
            HttpSession session) {
        List<CarritoDTO> carrito = (List<CarritoDTO>) session.getAttribute("carrito");

        if (carrito != null) {
            carrito.removeIf(item -> item.getProducto().getIdProducto().equals(productoId) &&
                    item.getColor().equalsIgnoreCase(color) &&
                    item.getTalla().equalsIgnoreCase(talla));
        }

        session.setAttribute("carrito", carrito);
        return "redirect:/carrito/ver";
    }

    @GetMapping("/ver")
    public String verCarrito(@ModelAttribute("carrito") List<CarritoDTO> carrito, Model model) {
        double subtotal = carrito.stream().mapToDouble(CarritoDTO::getSubtotal).sum();
        double descuento = subtotal * 0.20;
        double envio = 4.99;
        double total = subtotal - descuento + envio;

        model.addAttribute("carritoItems", carrito);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("descuento", descuento);
        model.addAttribute("envio", envio);
        model.addAttribute("total", total);
        return "carrito"; // Vista: carrito.html
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
