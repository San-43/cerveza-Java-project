package org.cerveza.cerveza.model;

public class Fabricante {
    private int idfabricante;
    private String nombre;
    private String pais;
    private String descripcion;

    public Fabricante() {}

    public Fabricante(int idfabricante, String nombre, String pais, String descripcion) {
        this.idfabricante = idfabricante;
        this.nombre = nombre;
        this.pais = pais;
        this.descripcion = descripcion;
    }

    public int getIdFabricante() {
        return idfabricante;
    }

    public void setIdfabricante(int idfabricante) {
        this.idfabricante = idfabricante;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
