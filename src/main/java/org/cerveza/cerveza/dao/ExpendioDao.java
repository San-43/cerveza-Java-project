package org.cerveza.cerveza.dao;

import org.cerveza.cerveza.model.Expendio;
import java.util.List;

public interface ExpendioDao {
    Expendio insert(Expendio e) throws Exception;
    boolean update(Expendio e) throws Exception;
    boolean delete(int idexpendio) throws Exception;
    Expendio findById(int idexpendio) throws Exception;
    List<Expendio> findAll() throws Exception;
}
