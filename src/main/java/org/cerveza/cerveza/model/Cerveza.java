package org.cerveza.cerveza.model;

public class Cerveza {
    private Integer id;
    private Integer idMarca;
    private String nombre;
    private String aspecto;
    private String procedimientos;
    private Double graduacion; // decimal(4,2)
    private Integer existenciaTotal;


    public Cerveza(Integer id, Integer idMarca, String nombre, String aspecto, String procedimientos, Double graduacion, Integer existenciaTotal) {
        this.id = id; this.idMarca = idMarca; this.nombre = nombre; this.aspecto = aspecto; this.procedimientos = procedimientos; this.graduacion = graduacion;
        this.existenciaTotal = existenciaTotal;
    }
    public Cerveza(Integer id, Integer idMarca, String nombre, String aspecto, String procedimientos, Double graduacion) {
        this.id = id; this.idMarca = idMarca; this.nombre = nombre; this.aspecto = aspecto; this.procedimientos = procedimientos; this.graduacion = graduacion;
    }
    public Cerveza() {}


    // Getters/Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getIdMarca() { return idMarca; }
    public void setIdMarca(Integer idMarca) { this.idMarca = idMarca; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getAspecto() { return aspecto; }
    public void setAspecto(String aspecto) { this.aspecto = aspecto; }
    public String getProcedimientos() { return procedimientos; }
    public void setProcedimientos(String procedimientos) { this.procedimientos = procedimientos; }
    public Double getGraduacion() { return graduacion; }
    public void setGraduacion(Double graduacion) { this.graduacion = graduacion; }
    public Integer getExistenciaTotal() { return existenciaTotal; }
    public void setExistenciaTotal(Integer existenciaTotal) { this.existenciaTotal = existenciaTotal; }

    @Override
    public String toString() {
        return "Cerveza{" +
                "id=" + id +
                ", idMarca=" + idMarca +
                ", nombre='" + nombre + '\'' +
                ", aspecto='" + aspecto + '\'' +
                ", procedimientos='" + procedimientos + '\'' +
                ", graduacion=" + graduacion +
                ", existenciaTotal=" + existenciaTotal +
                '}';
    }
}
