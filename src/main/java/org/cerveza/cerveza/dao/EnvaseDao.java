package org.cerveza.cerveza.dao;

import org.cerveza.cerveza.model.Envase;
import java.util.*;

public interface EnvaseDao {
    void insert(Envase e);
    void update(Envase e);
    void delete(int id);
    Optional<Envase> findById(int id);
    List<Envase> findAll();
}
