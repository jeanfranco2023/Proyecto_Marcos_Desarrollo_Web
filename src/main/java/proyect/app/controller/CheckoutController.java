package proyect.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;
import java.util.List;
import java.util.stream.Collectors;

import proyect.app.dto.CarritoDTO;
import proyect.app.service.CategoriaService;
import proyect.app.service.PedidoService;
import proyect.app.service.ProductoService;
import proyect.app.service.UsuarioService;
import proyect.app.repository.MetodoPagoRepository;
import proyect.app.repository.PagoRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import proyect.app.entity.Categoria;
import proyect.app.entity.DetallePedido;
import proyect.app.entity.MetodoPago;
import proyect.app.entity.Pago;
import proyect.app.entity.Pedido;
import proyect.app.entity.Productos;
import proyect.app.entity.Usuarios;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;

@Controller
@RequestMapping("/checkout")
@SessionAttributes("carrito")
@RequiredArgsConstructor
public class CheckoutController {

    private final PedidoService pedidoService;
    private final MetodoPagoRepository metodoPagoRepository;
    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;
    private final ProductoService productoService;
    private final PagoRepository pagoService;
    

    @GetMapping
    public String mostrarFormularioPago(@ModelAttribute("carrito") List<CarritoDTO> carrito, Model model) {
        double subtotal = carrito.stream().mapToDouble(CarritoDTO::getSubtotal).sum();
        double envio = 4.99;
        double total = Math.round((subtotal + envio) * 100.0) / 100.0;

        List<MetodoPago> metodos = metodoPagoRepository.findAll(); // Asegúrate que este método esté implementado

        model.addAttribute("carritoItems", carrito); // 👈 NECESARIO
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("envio", envio);
        model.addAttribute("total", total);
        model.addAttribute("metodosPago", metodos);

        return "checkout"; // tu plantilla checkout.html
    }

    @PostMapping("/procesar")
    public String procesarPago(
            @RequestParam("metodoPago") Integer metodoPagoId,
            @ModelAttribute("carrito") List<CarritoDTO> carrito,
            HttpSession session,
            RedirectAttributes redirect,
            Principal principal) {

        if (carrito == null || carrito.isEmpty()) {
            redirect.addFlashAttribute("error", "Tu carrito está vacío.");
            return "redirect:/carrito/ver";
        }

        Usuarios usuario = usuarioService.buscarPorcorreoUsuario(principal.getName());

        double subtotal = carrito.stream().mapToDouble(CarritoDTO::getSubtotal).sum();
        double envio = 4.99;
        double total = Math.round((subtotal + envio) * 100.0) / 100.0;

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFechaPedido(LocalDate.now());
        pedido.setEstadoPedido("Pagado");
        pedido.setMontoPedido(total);

        List<DetallePedido> detalles = new ArrayList<>();

        for (CarritoDTO item : carrito) {
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(item.getProducto());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioProducto(item.getProducto().getPrecioProducto());

            Productos producto = item.getProducto();
            producto.setStockProducto(producto.getStockProducto() - item.getCantidad());
            productoService.save(producto);

            detalles.add(detalle);
        }

        pedido.setDetalles(detalles);
        pedidoService.save(pedido);

        MetodoPago metodo = metodoPagoRepository.findById(metodoPagoId).orElse(null);
        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setMetodoPago(metodo);
        pago.setEstado("Pagado");

        pagoService.save(pago);

        carrito.clear();
        session.setAttribute("carrito", carrito);

        redirect.addFlashAttribute("pedidoId", pedido.getIdPedido());
        return "redirect:/checkout/pedido/confirmacion";
    }

    @GetMapping("/pedido/confirmacion")
    public String mostrarConfirmacion(@ModelAttribute("pedidoId") Integer pedidoId, Model model) {
        if (pedidoId == null) {
            return "redirect:/productos/ropa";
        }
        model.addAttribute("pedidoId", pedidoId);
        return "confirmacion";
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
}