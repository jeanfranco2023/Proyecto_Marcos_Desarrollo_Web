package proyect.app.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "usuarios")
public class Usuarios {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;
    private String nombreUsuario;
    private String apellidoUsuario;
    private int numeroTelefonoUsuario;
    private String correoUsuario;
    private String contrasenaUsuario;
    private boolean isAdmin;
    @JsonIgnore
    @OneToMany(mappedBy = "usuario")
    private List<Pedido> pedidos;
    @JsonIgnore
    @OneToOne(mappedBy = "usuario")
    private Carrito carrito;
}
