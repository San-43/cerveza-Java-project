package org.cerveza.cerveza.dao;

import org.cerveza.cerveza.model.Orden;

import java.util.List;

public interface OrdenDao {
    void insert (Orden orden) throws Exception;
    void update (Orden orden) throws Exception;
    void delete (int id) throws Exception;
    List<Orden> findAll() throws Exception;

}
