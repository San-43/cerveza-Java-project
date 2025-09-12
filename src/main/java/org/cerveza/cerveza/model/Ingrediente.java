package org.cerveza.cerveza.model;

public class Ingrediente {
    private Integer idIngrediente;
    private String nombre;
    private String descripcion;

    public Ingrediente(Integer idIngrediente, String nombre, String descripcion) {
        this.idIngrediente = idIngrediente;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Ingrediente() {
        this.idIngrediente = null;
        this.nombre = "";
        this.descripcion = "";
    }

    public Integer getIdIngrediente() {
        return idIngrediente;
    }

    public void setIdIngrediente(Integer idIngrediente) {
        this.idIngrediente = idIngrediente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
