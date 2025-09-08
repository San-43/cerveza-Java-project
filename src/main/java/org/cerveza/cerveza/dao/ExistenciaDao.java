package org.cerveza.cerveza.dao;

import org.cerveza.cerveza.model.Existencia;
import java.util.List;
import java.util.Optional;

public interface ExistenciaDao {
    List<Existencia> findAll();                 // solo ids
    List<Existencia> findAllWithLabels();       // con nombres (join)
    Optional<Existencia> findById(int id);
    int insert(Existencia e);
    boolean update(Existencia e);
    boolean delete(int id);
}
