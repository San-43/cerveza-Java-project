package org.cerveza.cerveza.dao;

import org.cerveza.cerveza.model.Expendio;
import org.cerveza.cerveza.model.Ingrediente;

import java.util.List;

public interface IngredienteDao {
    void insert(Ingrediente ingrediente);
    boolean update (Ingrediente ingrediente) throws Exception;
    boolean delete (int id) throws Exception;
    List<Ingrediente> findAll() throws Exception;

}
