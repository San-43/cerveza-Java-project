package org.cerveza.cerveza.model;

import java.time.LocalDate;

public class Existencia {
    private int idExistencia;
    private int idExpendio;
    private int idPresentacion;
    private int cantidad;
    private LocalDate fecha;

    private String expendioNombre;       // label opcional (join)
    private String presentacionNombre;   // label opcional (join)

    public Existencia(int idExistencia, int idExpendio, int idPresentacion, int cantidad, LocalDate fecha,
                      String expendioNombre, String presentacionNombre) {
        this.idExistencia = idExistencia;
        this.idExpendio = idExpendio;
        this.idPresentacion = idPresentacion;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.expendioNombre = expendioNombre;
        this.presentacionNombre = presentacionNombre;
    }

    public int getIdExistencia() { return idExistencia; }
    public void setIdExistencia(int idExistencia) { this.idExistencia = idExistencia; }

    public int getIdExpendio() { return idExpendio; }
    public void setIdExpendio(int idExpendio) { this.idExpendio = idExpendio; }

    public int getIdPresentacion() { return idPresentacion; }
    public void setIdPresentacion(int idPresentacion) { this.idPresentacion = idPresentacion; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getExpendioNombre() { return expendioNombre; }
    public void setExpendioNombre(String expendioNombre) { this.expendioNombre = expendioNombre; }

    public String getPresentacionNombre() { return presentacionNombre; }
    public void setPresentacionNombre(String presentacionNombre) { this.presentacionNombre = presentacionNombre; }
}
