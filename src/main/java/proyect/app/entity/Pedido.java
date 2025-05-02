package proyect.app.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "pedidos")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPedido;
    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuarios usuario;
    private LocalDate fechaPedido;
    private String estadoPedido;
    private double montoPedido;
    @OneToMany(mappedBy = "pedido")
    private List<DetallePedido> detalles;
    @OneToOne(mappedBy = "pedido")
    private Pago pago;
}
