package org.cerveza.cerveza.model;

public class Pedido {
    private int idpedido;
    private int idexpendio;

    public Pedido() {}

    public Pedido(int idpedido, int idexpendio) {
        this.idpedido = idpedido;
        this.idexpendio = idexpendio;
    }

    public int getIdpedido() {
        return idpedido;
    }

    public void setIdpedido(int idpedido) {
        this.idpedido = idpedido;
    }

    public int getIdexpendio() {
        return idexpendio;
    }

    public void setIdexpendio(int idexpendio) {
        this.idexpendio = idexpendio;
    }
}

