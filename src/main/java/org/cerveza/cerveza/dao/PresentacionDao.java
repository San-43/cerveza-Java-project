package org.cerveza.cerveza.dao;

import org.cerveza.cerveza.model.Presentacion;
import java.util.List;
import java.util.Optional;

public interface PresentacionDao {
    List<Presentacion> findAll();                       // solo ids
    List<Presentacion> findAllWithLabels();             // con nombres (join)
    Optional<Presentacion> findById(int id);
    int insert(Presentacion p);                          // retorna id generado
    boolean update(Presentacion p);
    boolean delete(int id);
}
