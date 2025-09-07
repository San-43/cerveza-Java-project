package org.cerveza.cerveza.model;

public class Envase {
    private Integer id;
    private String nombre;
    private String material;
    private String capacidad;
    private String descripcion;


    public Envase() {}
    public Envase(Integer id, String nombre, String material, String capacidad, String descripcion) {
        this.id=id; this.nombre=nombre; this.material=material; this.capacidad=capacidad; this.descripcion=descripcion;
    }


    // Getters/Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }
    public String getCapacidad() { return capacidad; }
    public void setCapacidad(String capacidad) { this.capacidad = capacidad; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
