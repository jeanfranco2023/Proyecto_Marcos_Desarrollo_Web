package proyect.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.InputStream;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.FileNotFoundException;

import proyect.app.dto.CarritoDTO;
import proyect.app.dto.ReportProductDetail;
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
import java.util.HashMap;
import org.springframework.http.MediaType;

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

        List<MetodoPago> metodos = metodoPagoRepository.findAll();

        model.addAttribute("carritoItems", carrito);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("envio", envio);
        model.addAttribute("total", total);
        model.addAttribute("metodosPago", metodos);

        return "checkout";
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

    @GetMapping("/reporte/{id}")
    public void generarReportePedido(@PathVariable Integer id, HttpServletResponse response) throws Exception {

        Pedido pedido = pedidoService.buscarPorId(id);
        if (pedido == null) {
            throw new RuntimeException("Pedido no encontrado con ID: " + id);
        }

        List<ReportProductDetail> productDetails = new ArrayList<>();
        if (pedido.getDetalles() != null) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                productDetails.add(new ReportProductDetail(
                        detalle.getProducto().getNombreProducto(),
                        detalle.getProducto().getTallasText(),
                        detalle.getProducto().getColoresText(),
                        detalle.getCantidad(),
                        detalle.getPrecioProducto().doubleValue()));
            }
        } else {
            System.out.println("Advertencia: El pedido ID " + id + " no tiene detalles de pedido.");
        }

        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(productDetails);

        InputStream imageStream = getClass().getClassLoader().getResourceAsStream("static/img/logo.jpg");
        if (imageStream == null) {
            throw new FileNotFoundException("Imagen del logo no encontrada en resources/static/img/logo.jpg");
        }

        InputStream reporteStream = getClass().getClassLoader().getResourceAsStream("report/reporteCompra.jrxml");
        if (reporteStream == null) {
            throw new FileNotFoundException("No se encontró el archivo reporteCompra.jrxml en resources/report/");
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(reporteStream);
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("ds", ds);
        

        Double montoPedidoDouble = pedido.getMontoPedido();
        Double envioValue = 4.99;
        Double subtotalValue = montoPedidoDouble - envioValue;

        parameters.put("subtotal", subtotalValue);
        parameters.put("envio", envioValue);
        parameters.put("total", montoPedidoDouble);

        parameters.put("imagenLogo", imageStream);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader("Content-Disposition", "inline; filename=FacturaCompra.pdf");
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());

        try { if (imageStream != null) imageStream.close(); } catch (Exception e) { /* log error */ }
        try { if (reporteStream != null) reporteStream.close(); } catch (Exception e) { /* log error */ }
    }

     @GetMapping("/reporte-carrito")
    public void generarReporteCarrito(@ModelAttribute("carrito") List<CarritoDTO> carrito, HttpServletResponse response) throws Exception {

        if (carrito == null || carrito.isEmpty()) {
            throw new RuntimeException("El carrito está vacío, no se puede generar el reporte.");
        }

        List<ReportProductDetail> productDetails = new ArrayList<>();
        double subtotalReport = 0.0;
        for (CarritoDTO item : carrito) {
            productDetails.add(new ReportProductDetail(
                    item.getProducto().getNombreProducto(),
                    item.getProducto().getTallasText(),
                    item.getProducto().getColoresText(),
                    item.getCantidad(),
                    item.getProducto().getPrecioProducto()));
            subtotalReport += item.getSubtotal();
        }

        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(productDetails);

        InputStream imageStream = getClass().getClassLoader().getResourceAsStream("static/img/logo.jpg");
        if (imageStream == null) {
            throw new FileNotFoundException("Imagen del logo no encontrada en resources/static/img/logo.jpg");
        }

        InputStream reporteStream = getClass().getClassLoader().getResourceAsStream("report/reporteCompra.jrxml");
        if (reporteStream == null) {
            throw new FileNotFoundException("No se encontró el archivo reporteCompra.jrxml en resources/report/");
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(reporteStream);
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("ds", ds);

        double envioValue = 4.99;
        double totalValue = Math.round((subtotalReport + envioValue) * 100.0) / 100.0;

        parameters.put("subtotal", subtotalReport);
        parameters.put("envio", envioValue);
        parameters.put("total", totalValue);

        parameters.put("imagenLogo", imageStream);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader("Content-Disposition", "inline; filename=ResumenCarrito.pdf");
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());

        try { if (imageStream != null) imageStream.close(); } catch (Exception e) { /* log error */ }
        try { if (reporteStream != null) reporteStream.close(); } catch (Exception e) { /* log error */ }
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

    @ModelAttribute("carrito")
    public List<CarritoDTO> carrito() {
        return new ArrayList<>();
    }
}