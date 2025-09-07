package org.cerveza.cerveza.dao;

import org.cerveza.cerveza.model.Cerveza;

import java.util.List;
import java.util.Optional;


public interface CervezaDao {
    void insert(Cerveza c);
    void update(Cerveza c);
    void delete(int id);
    Optional<Cerveza> findById(int id);
    List<Cerveza> findAll();
}
