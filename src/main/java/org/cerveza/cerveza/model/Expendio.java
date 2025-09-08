package org.cerveza.cerveza.model;

public class Expendio {
    private Integer idexpendio;
    private String nombre;
    private String ubicacion;
    private String rfc;
    private String responsable;

    public Integer getIdexpendio() { return idexpendio; }
    public void setIdexpendio(Integer idexpendio) { this.idexpendio = idexpendio; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getRfc() { return rfc; }
    public void setRfc(String rfc) { this.rfc = rfc; }

    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }
}
