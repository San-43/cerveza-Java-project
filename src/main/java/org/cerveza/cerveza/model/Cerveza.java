package org.cerveza.cerveza.model;

public class Cerveza {
    private Integer id;
    private Integer idMarca;
    private String nombre;
    private String aspecto;
    private String procedimientos;
    private Double graduacion; // decimal(4,2)


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
}
