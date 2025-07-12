package proyect.app.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import proyect.app.repository.PedidoRepository;
import proyect.app.repository.CarritoRepository;
import proyect.app.entity.Pedido;
import proyect.app.entity.Carrito;
import proyect.app.entity.DetallePedido;
import proyect.app.entity.DetalleCarrito;
import proyect.app.entity.Productos;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    @Autowired
    private PedidoRepository pedidoRepo;
    @Autowired
    private ProductoService productoService;
    @Autowired
    private CarritoRepository carritoRepo; 

    @Transactional
    public Pedido procesarPedido(Carrito carrito, double subtotal,
            double descuento, double envio, double total) {

        Pedido pedido = new Pedido();
        pedido.setUsuario(carrito.getUsuario());
        pedido.setFechaPedido(LocalDate.now());
        pedido.setEstadoPedido("PENDIENTE");
        pedido.setMontoPedido(total);

        List<DetallePedido> detalles = new ArrayList<>();

        for (DetalleCarrito dc : carrito.getDetalles()) {
            Productos prod = dc.getProducto();

            if (prod.getStockProducto() < dc.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para " + prod.getNombreProducto());
            }
            prod.setStockProducto(prod.getStockProducto() - dc.getCantidad());
            productoService.save(prod);

            DetallePedido det = new DetallePedido();
            det.setPedido(pedido);
            det.setProducto(prod);
            det.setCantidad(dc.getCantidad());
            det.setPrecioProducto(prod.getPrecioProducto());

            detalles.add(det);
        }

        pedido.setDetalles(detalles);
        Pedido guardado = pedidoRepo.save(pedido);

        carrito.getDetalles().clear();
        carritoRepo.save(carrito);

        return guardado;
    }
}
