package proyect.app.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ProductoForm {
    private Integer idProducto;
    private String nombreProducto;
    private String descripcionProducto;
    private double precioProducto;
    private double estrellasProducto;
    private int stockProducto;
    private MultipartFile imagenProducto;
    private List<Integer> categoriasSeleccionadas;
    private List<Integer> tallasSeleccionadas;
    private List<Integer> coloresSeleccionados;
    
    public ProductoForm() {
        this.tallasSeleccionadas = new ArrayList<>();
        this.coloresSeleccionados = new ArrayList<>();
        this.categoriasSeleccionadas = new ArrayList<>();
    }
}
