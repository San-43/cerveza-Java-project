package org.cerveza.cerveza.dao;

import org.cerveza.cerveza.model.Venta;
import java.util.List;
import java.util.Optional;

public interface VentaDao {
    List<Venta> findAll();
    Optional<Venta> findById(int idventa);
    Venta insert(Venta v);
    boolean update(Venta v);
    boolean delete(int idventa);
}
