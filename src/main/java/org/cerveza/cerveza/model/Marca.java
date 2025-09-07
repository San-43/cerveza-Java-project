package org.cerveza.cerveza.model;

public class Marca {
    private Integer id;
    private Integer idFabricante;
    private String nombre;
    private String descripcion;


    public Marca() {}
    public Marca(Integer id, Integer idFabricante, String nombre, String descripcion) {
        this.id=id; this.idFabricante=idFabricante; this.nombre=nombre; this.descripcion=descripcion;
    }


    // Getters/Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getIdFabricante() { return idFabricante; }
    public void setIdFabricante(Integer idFabricante) { this.idFabricante = idFabricante; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
