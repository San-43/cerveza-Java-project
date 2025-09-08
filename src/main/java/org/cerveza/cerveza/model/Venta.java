package org.cerveza.cerveza.model;

import java.time.LocalDate;

public class Venta {
    private Integer idventa;
    private Integer idexpendio;
    private Integer idpresentacion;
    private LocalDate fecha;
    private Integer cantidad;

    public Venta() {}

    public Venta(Integer idventa, Integer idexpendio, Integer idpresentacion, LocalDate fecha, Integer cantidad) {
        this.idventa = idventa;
        this.idexpendio = idexpendio;
        this.idpresentacion = idpresentacion;
        this.fecha = fecha;
        this.cantidad = cantidad;
    }

    public Integer getIdventa() { return idventa; }
    public void setIdventa(Integer idventa) { this.idventa = idventa; }
    public Integer getIdexpendio() { return idexpendio; }
    public void setIdexpendio(Integer idexpendio) { this.idexpendio = idexpendio; }
    public Integer getIdpresentacion() { return idpresentacion; }
    public void setIdpresentacion(Integer idpresentacion) { this.idpresentacion = idpresentacion; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
