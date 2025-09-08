package org.cerveza.cerveza.model;

import java.time.LocalDate;

public class Existencia {
    private Integer idExistencia;
    private Integer idExpendio;
    private Integer idPresentacion;
    private Integer cantidad;
    private LocalDate fecha;

    public Existencia(Integer idExistencia, Integer idExpendio, Integer idPresentacion, Integer cantidad, LocalDate fecha) {
        this.idExistencia = idExistencia;
        this.idExpendio = idExpendio;
        this.idPresentacion = idPresentacion;
        this.cantidad = cantidad;
        this.fecha = fecha;
    }

    public Integer getIdExistencia() {
        return idExistencia;
    }

    public void setIdExistencia(Integer idExistencia) {
        this.idExistencia = idExistencia;
    }

    public Integer getIdExpendio() {
        return idExpendio;
    }

    public void setIdExpendio(Integer idExpendio) {
        this.idExpendio = idExpendio;
    }

    public Integer getIdPresentacion() {
        return idPresentacion;
    }

    public void setIdPresentacion(Integer idPresentacion) {
        this.idPresentacion = idPresentacion;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
