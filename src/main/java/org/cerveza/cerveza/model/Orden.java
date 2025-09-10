package org.cerveza.cerveza.model;

import java.sql.Date;

public class Orden {
    Integer idOrden;
    Integer idPresentacion;
    Integer cantidad;
    Date fecha_orden;
    Date fecha_despacho;

    public Orden(Integer idOrden, Integer idPresentacion, Integer cantidad, Date fecha_orden, Date fecha_despacho) {
        this.idOrden = idOrden;
        this.idPresentacion = idPresentacion;
        this.cantidad = cantidad;
        this.fecha_orden = fecha_orden;
        this.fecha_despacho = fecha_despacho;
    }

    public Orden(Integer idPresentacion, Integer cantidad, Date fecha_orden, Date fecha_despacho) {
        this.idPresentacion = idPresentacion;
        this.cantidad = cantidad;
        this.fecha_orden = fecha_orden;
        this.fecha_despacho = fecha_despacho;
    }

    public Integer getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(Integer idOrden) {
        this.idOrden = idOrden;
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

    public Date getFecha_orden() {
        return fecha_orden;
    }

    public void setFecha_orden(Date fecha_orden) {
        this.fecha_orden = fecha_orden;
    }

    public Date getFecha_despacho() {
        return fecha_despacho;
    }

    public void setFecha_despacho(Date fecha_despacho) {
        this.fecha_despacho = fecha_despacho;
    }
}
