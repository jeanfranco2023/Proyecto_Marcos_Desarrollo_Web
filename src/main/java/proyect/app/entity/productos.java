package proyect.app.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = { "tallas", "colores", "categorias" })
@Table(name = "productos")
@EqualsAndHashCode(exclude = { "tallas", "colores", "categorias" })
public class Productos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProducto;

    private String nombreProducto;
    private String descripcionProducto;
    private double precioProducto;
    private int stockProducto;
    private double estrellasProducto;
    private String imagenProducto;

    @ManyToMany
    @JoinTable(name = "producto_talla", joinColumns = @JoinColumn(name = "producto_id"), inverseJoinColumns = @JoinColumn(name = "talla_id"))
    private Set<Talla> tallas = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "producto_color", joinColumns = @JoinColumn(name = "producto_id"), inverseJoinColumns = @JoinColumn(name = "color_id"))
    @JsonManagedReference
    private Set<Colores> colores = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "producto_categoria", joinColumns = @JoinColumn(name = "id_producto"), inverseJoinColumns = @JoinColumn(name = "id_categoria"))
    private Set<Categoria> categorias = new HashSet<>();

    public String getCategoriasText() {
        return categorias != null ? categorias.stream()
                .map(Categoria::getNombreCategoria)
                .collect(Collectors.joining(", ")) : "";
    }

    public String getTallasText() {
        return tallas != null ? tallas.stream()
                .map(Talla::getValor)
                .collect(Collectors.joining(", ")) : "";
    }

    public String getColoresText() {
        return colores != null ? colores.stream()
                .map(Colores::getNombre)
                .collect(Collectors.joining(", ")) : "";
    }

    public String generarEstrellas() {
        double puntuacion = this.estrellasProducto;
        StringBuilder estrellas = new StringBuilder();

        for (int i = 1; i <= 5; i++) {
            if (puntuacion >= i) {
                estrellas.append("<i class='bi bi-star-fill me-1'></i>");
            } else if (puntuacion > i - 1) {
                estrellas.append("<i class='bi bi-star-half me-1'></i>");
            } else {
                estrellas.append("<i class='bi bi-star me-1'></i>");
            }
        }

        estrellas.append("<span class='fw-bold text-dark ms-2'>")
                .append(puntuacion)
                .append("/5</span>");

        return estrellas.toString();
    }
}