package proyect.app.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import proyect.app.entity.Productos;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoDTO {
    private Productos producto;
    private String talla;
    private String color;
    private int cantidad;
    private String imagen;

    public double getSubtotal() {
        return cantidad * producto.getPrecioProducto();
    }
}
