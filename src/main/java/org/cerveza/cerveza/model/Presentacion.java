package org.cerveza.cerveza.model;

public class Presentacion {
    private int idPresentacion;
    private int idEnvase;
    private int idCerveza;
    private String envaseNombre;   // label opcional (join)
    private String cervezaNombre;  // label opcional (join)

    public Presentacion(int idPresentacion, int idEnvase, int idCerveza, String envaseNombre, String cervezaNombre) {
        this.idPresentacion = idPresentacion;
        this.idEnvase = idEnvase;
        this.idCerveza = idCerveza;
        this.envaseNombre = envaseNombre;
        this.cervezaNombre = cervezaNombre;
    }

    public int getIdPresentacion() { return idPresentacion; }
    public void setIdPresentacion(int idPresentacion) { this.idPresentacion = idPresentacion; }

    public int getIdEnvase() { return idEnvase; }
    public void setIdEnvase(int idEnvase) { this.idEnvase = idEnvase; }

    public int getIdCerveza() { return idCerveza; }
    public void setIdCerveza(int idCerveza) { this.idCerveza = idCerveza; }

    public String getEnvaseNombre() { return envaseNombre; }
    public void setEnvaseNombre(String envaseNombre) { this.envaseNombre = envaseNombre; }

    public String getCervezaNombre() { return cervezaNombre; }
    public void setCervezaNombre(String cervezaNombre) { this.cervezaNombre = cervezaNombre; }
}
