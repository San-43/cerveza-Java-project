package org.cerveza.cerveza.model;

import java.time.LocalDate;

public class Produccion {
    private Integer id;
    private Integer cervezaId;
    private LocalDate fecha;
    private Integer cantidad;

    // Solo para mostrar en la tabla/combobox
    private String cervezaNombre;

    public Produccion() {}

    public Produccion(Integer id, Integer cervezaId, LocalDate fecha, Integer cantidad, String cervezaNombre) {
        this.id = id;
        this.cervezaId = cervezaId;
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.cervezaNombre = cervezaNombre;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getCervezaId() { return cervezaId; }
    public void setCervezaId(Integer cervezaId) { this.cervezaId = cervezaId; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public String getCervezaNombre() { return cervezaNombre; }
    public void setCervezaNombre(String cervezaNombre) { this.cervezaNombre = cervezaNombre; }
}
