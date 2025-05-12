package proyect.app.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import proyect.app.entity.Categoria;
import proyect.app.entity.Colores;
import proyect.app.entity.Productos;
import proyect.app.entity.Talla;

@Data
public class ProductoDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;
    private String imagen;
    private List<String> categorias;
    private List<String> tallas;
    private List<String> colores;

    public ProductoDTO(Productos producto) {
        this.id = producto.getIdProducto();
        this.nombre = producto.getNombreProducto();
        this.descripcion = producto.getDescripcionProducto();
        this.precio = producto.getPrecioProducto();
        this.stock = producto.getStockProducto();
        this.imagen = producto.getImagenProducto();
        this.categorias = producto.getCategorias().stream()
                .map(Categoria::getNombreCategoria)
                .collect(Collectors.toList());
        this.tallas = producto.getTallas().stream()
                .map(Talla::getValor)
                .collect(Collectors.toList());
        this.colores = producto.getColores().stream()
                .map(Colores::getNombre)
                .collect(Collectors.toList());
    }

    public void setIdProducto(Integer id) {
        this.id = id;
    }

    public void setNombreProducto(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcionProducto(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setPrecioProducto(double precio) {
        this.precio = precio;
    }

    public void setStockProducto(int stock) {
        this.stock = stock;
    }

    public void setImagenProducto(String imagen) {
        this.imagen = imagen;
    }

    public void setCategorias(List<Integer> categorias) {
        this.categorias = categorias.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
    }

    public void setTallas(List<Integer> tallas) {
        this.tallas = tallas.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
    }

    public void setColores(List<Integer> colores) {
        this.colores = colores.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
    }

}
