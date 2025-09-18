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
    List<Cerveza> findByIdMarca(int idMarca);
    List<Cerveza> findByNombre(String nombre);
    List<Cerveza> findByAspecto(String aspecto);
    List<Cerveza> findByGraduacion(Double graduacion);
    List<Cerveza> findByExistenciaTotal(int existenciaTotal);
    List<Cerveza> findByIdLike(String id);
    List<Cerveza> findByIdMarcaLike(String idMarca);
    List<Cerveza> findByNombreLike(String nombre);
    List<Cerveza> findByAspectoLike(String aspecto);
    List<Cerveza> findByGraduacionLike(String graduacion);
    List<Cerveza> findByExistenciaTotalLike(String existenciaTotal);
}
