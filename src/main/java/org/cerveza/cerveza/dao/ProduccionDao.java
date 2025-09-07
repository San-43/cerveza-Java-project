package org.cerveza.cerveza.dao;

import org.cerveza.cerveza.model.Produccion;
import java.util.List;

public interface ProduccionDao {
    List<Produccion> findAll();
    void insert(Produccion p);
    void update(Produccion p);
    void delete(int id);
}
