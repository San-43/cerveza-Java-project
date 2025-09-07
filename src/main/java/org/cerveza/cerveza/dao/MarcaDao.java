package org.cerveza.cerveza.dao;

import org.cerveza.cerveza.model.Marca;
import java.util.*;

public interface MarcaDao {
    void insert(Marca m);
    void update(Marca m);
    void delete(int id);
    Optional<Marca> findById(int id);
    List<Marca> findAll();
}
