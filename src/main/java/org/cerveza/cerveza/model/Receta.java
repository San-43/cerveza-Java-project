package org.cerveza.cerveza.model;

public class Receta {
    private Integer idreceta;
    private Integer idcerveza;
    private Integer idingrediente;
    private Integer cantidad;

    public Receta() {}

    public Receta(Integer idreceta, Integer idcerveza, Integer idingrediente, Integer cantidad) {
        this.idreceta = idreceta;
        this.idcerveza = idcerveza;
        this.idingrediente = idingrediente;
        this.cantidad = cantidad;
    }

    public Integer getIdreceta() { return idreceta; }
    public void setIdreceta(Integer idreceta) { this.idreceta = idreceta; }
    public Integer getIdcerveza() { return idcerveza; }
    public void setIdcerveza(Integer idcerveza) { this.idcerveza = idcerveza; }
    public Integer getIdingrediente() { return idingrediente; }
    public void setIdingrediente(Integer idingrediente) { this.idingrediente = idingrediente; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
