package org.cerveza.cerveza.dao;

import org.cerveza.cerveza.model.Receta;
import java.util.List;
import java.util.Optional;

public interface RecetaDao {
    List<Receta> findAll();
    Optional<Receta> findById(int idreceta);
    Receta insert(Receta r);
    boolean update(Receta r);
    boolean delete(int idreceta);
}
