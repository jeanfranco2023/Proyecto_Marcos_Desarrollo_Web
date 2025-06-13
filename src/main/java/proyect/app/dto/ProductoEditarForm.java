package proyect.app.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoEditarForm {

    private Integer idProducto;
    private String nombreProducto;
    private String descripcionProducto;
    private MultipartFile imagenProducto;
    private Double precioProducto;
    private Double estrellas;
    private Integer stockProducto;
    private Integer categoriasSeleccionadas;
    private String tallasSeleccionadas;
    private String coloresSeleccionados;

}
