package proyect.app.dto;


public class ReportProductDetail {
    private String nombreProducto;
    private String tallaProducto;
    private String colorProducto;
    private Integer cantidadProducto;
    private double precioUnitarioProducto;

    public ReportProductDetail(String nombreProducto, String tallaProducto, String colorProducto,
            Integer cantidadProducto, double precioUnitarioProducto) {
        this.nombreProducto = nombreProducto;
        this.tallaProducto = tallaProducto;
        this.colorProducto = colorProducto;
        this.cantidadProducto = cantidadProducto;
        this.precioUnitarioProducto = precioUnitarioProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public String getTallaProducto() {
        return tallaProducto;
    }

    public String getColorProducto() {
        return colorProducto;
    }

    public Integer getCantidadProducto() {
        return cantidadProducto;
    }

    public double getPrecioUnitarioProducto() {
        return precioUnitarioProducto;
    }
}
