package org.cerveza.cerveza.dao;

import org.cerveza.cerveza.model.Pedido;
import java.util.List;
import java.util.Optional;

public interface PedidoDao {
    List<Pedido> findAll();
    Optional<Pedido> findById(int idpedido);
    Pedido insert(Pedido p);
    boolean update(Pedido p);
    boolean delete(int idpedido);
}

