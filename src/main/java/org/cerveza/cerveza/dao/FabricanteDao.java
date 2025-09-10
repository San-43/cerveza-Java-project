package org.cerveza.cerveza.dao;

import org.cerveza.cerveza.model.Fabricante;
import java.util.List;

public interface FabricanteDao {
    void insertar(Fabricante fabricante);
    void actualizar(Fabricante fabricante);
    void eliminar(int idFabricante);
    Fabricante obtenerPorId(int idFabricante);
    List<Fabricante> obtenerTodos();
}

