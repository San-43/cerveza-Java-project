package org.cerveza.cerveza.model;

import java.time.LocalDate;

public class Produccion {
    private Integer id;            // id_produccion
    private Integer cervezaId;     // FK a cerveza.id_cerveza
    private String cervezaNombre;  // solo para mostrar en tabla (JOIN)
    private LocalDate fecha;
    private Integer cantidad;      // litros / unidades
    private String lote;

    public Produccion() {}

    public Produccion(Integer id, Integer cervezaId, String cervezaNombre,
                      LocalDate fecha, Integer cantidad, String lote) {
        this.id = id;
        this.cervezaId = cervezaId;
        this.cervezaNombre = cervezaNombre;
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.lote = lote;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getCervezaId() { return cervezaId; }
    public void setCervezaId(Integer cervezaId) { this.cervezaId = cervezaId; }

    public String getCervezaNombre() { return cervezaNombre; }
    public void setCervezaNombre(String cervezaNombre) { this.cervezaNombre = cervezaNombre; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public String getLote() { return lote; }
    public void setLote(String lote) { this.lote = lote; }
}
