package proyect.app.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalDate;

import proyect.app.repository.PedidoRepository;
import proyect.app.repository.DetallePedidoRepository;
import proyect.app.repository.PagoRepository;
import proyect.app.repository.MetodoPagoRepository;

import proyect.app.dto.CarritoDTO;
import proyect.app.entity.Usuarios;
import proyect.app.entity.Pedido;
import proyect.app.entity.Productos;
import proyect.app.entity.DetallePedido;
import proyect.app.entity.MetodoPago;
import proyect.app.entity.Pago;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final PagoRepository pagoRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final ProductoService productoService;
    
    @Transactional
    public void procesarPedido(List<CarritoDTO> carrito, Usuarios usuario, Integer metodoPagoId) {
        double subtotal = carrito.stream().mapToDouble(CarritoDTO::getSubtotal).sum();

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFechaPedido(LocalDate.now());
        pedido.setEstadoPedido("PENDIENTE");
        pedido.setMontoPedido(subtotal);
        pedido = pedidoRepository.save(pedido);

        for (CarritoDTO item : carrito) {
            Productos producto = item.getProducto();
            // Validar stock
            if (producto.getStockProducto() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombreProducto());
            }
            producto.setStockProducto(producto.getStockProducto() - item.getCantidad());
            productoService.save(producto);

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioProducto(producto.getPrecioProducto());
            detallePedidoRepository.save(detalle);
        }

        MetodoPago metodo = metodoPagoRepository.findById(metodoPagoId)
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));

        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setMetodoPago(metodo);
        pago.setEstado("PENDIENTE");
        pagoRepository.save(pago);
    }

    public void save(Pedido pedido) {
        pedidoRepository.save(pedido);
    }

    public Pedido buscarPorId(Integer id) {
        return pedidoRepository.findById(id).orElse(null);
    }
}