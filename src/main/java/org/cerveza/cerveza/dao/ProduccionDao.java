package org.cerveza.cerveza.dao;

import org.cerveza.cerveza.model.Produccion;
import java.util.List;

public interface ProduccionDao {
    Produccion insert(Produccion p) throws Exception;
    boolean update(Produccion p) throws Exception;
    boolean delete(int id) throws Exception;
    Produccion findById(int id) throws Exception;
    List<Produccion> findAll() throws Exception;
    List<Produccion> findByCerveza(int cervezaId) throws Exception;
}
