package org.cerveza.cerveza.dao.impl;

import org.cerveza.cerveza.config.Database;
import org.cerveza.cerveza.dao.CervezaDao;
import org.cerveza.cerveza.model.Cerveza;

import java.sql.*;
import java.util.*;


public class CervezaDaoImpl implements CervezaDao {
    public CervezaDaoImpl() {
        // Mostrar nombres de columnas de la tabla cerveza para depuración
        try (Connection cn = org.cerveza.cerveza.config.Database.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM cerveza LIMIT 1")) {
            java.sql.ResultSetMetaData meta = rs.getMetaData();
            System.out.println("Columnas de la tabla cerveza:");
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                System.out.println("- " + meta.getColumnName(i));
            }
        } catch (Exception e) {
            System.out.println("No se pudo obtener columnas de la tabla cerveza: " + e.getMessage());
        }
    }

    @Override
    public void insert(Cerveza c) {
        String sql = "INSERT INTO cerveza(idmarca, nombre, aspecto, procedimientos, graduacion) VALUES(?,?,?,?,?)";
        try (Connection cn = Database.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, c.getIdMarca());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getAspecto());
            ps.setString(4, c.getProcedimientos());
            if (c.getGraduacion() == null) ps.setNull(5, Types.DECIMAL); else ps.setDouble(5, c.getGraduacion());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }


    @Override
    public void update(Cerveza c) {
        String sql = "UPDATE cerveza SET idmarca=?, nombre=?, aspecto=?, procedimientos=?, graduacion=? WHERE idcerveza=?";
        try (Connection cn = Database.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, c.getIdMarca());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getAspecto());
            ps.setString(4, c.getProcedimientos());
            if (c.getGraduacion() == null) ps.setNull(5, Types.DECIMAL); else ps.setDouble(5, c.getGraduacion());
            ps.setInt(6, c.getId());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }


    @Override
    public void delete(int id) {
        String sql = "DELETE FROM cerveza WHERE idcerveza=?";
        try (Connection cn = Database.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }


    @Override
    public Optional<Cerveza> findById(int id) {
        String sql = "SELECT idcerveza, idmarca, nombre, aspecto, procedimientos, graduacion, existencia_total FROM cerveza WHERE idcerveza=?";
        try (Connection cn = Database.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Cerveza c = new Cerveza(
                        rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5),
                        rs.getObject(6) == null ? null : rs.getDouble(6), rs.getInt(7)
                );
                return Optional.of(c);
            }
            return Optional.empty();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }


    @Override
    public List<Cerveza> findAll() {
        String sql = "SELECT idcerveza, idmarca, nombre, aspecto, procedimientos, graduacion, existencia_total FROM cerveza ORDER BY idcerveza DESC";
        List<Cerveza> list = new ArrayList<>();
        try (Connection cn = Database.getConnection(); Statement st = cn.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                list.add(new Cerveza(
                        rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5),
                        rs.getObject(6) == null ? null : rs.getDouble(6), rs.getInt(7)
                ));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    // --- Métodos de búsqueda por campo ---
    @Override
    public List<Cerveza> findByIdMarca(int idMarca) {
        return findByField("idmarca", idMarca);
    }
    @Override
    public List<Cerveza> findByNombre(String nombre) {
        return findByField("nombre", nombre);
    }
    @Override
    public List<Cerveza> findByAspecto(String aspecto) {
        return findByField("aspecto", aspecto);
    }
    @Override
    public List<Cerveza> findByGraduacion(Double graduacion) {
        return findByField("graduacion", graduacion);
    }
    @Override
    public List<Cerveza> findByExistenciaTotal(int existenciaTotal) {
        return findByField("existencia_total", existenciaTotal);
    }
    @Override
    public List<Cerveza> findByIdLike(String id) {
        return findByFieldLike("idcerveza", id);
    }
    @Override
    public List<Cerveza> findByIdMarcaLike(String idMarca) {
        return findByFieldLike("idmarca", idMarca);
    }
    @Override
    public List<Cerveza> findByNombreLike(String nombre) {
        return findByFieldLike("nombre", nombre);
    }
    @Override
    public List<Cerveza> findByAspectoLike(String aspecto) {
        return findByFieldLike("aspecto", aspecto);
    }
    @Override
    public List<Cerveza> findByGraduacionLike(String graduacion) {
        return findByFieldLike("graduacion", graduacion);
    }
    @Override
    public List<Cerveza> findByExistenciaTotalLike(String existenciaTotal) {
        return findByFieldLike("existencia_total", existenciaTotal);
    }

    // Métodos utilitarios para evitar repetición
    private List<Cerveza> findByField(String field, Object value) {
        String sql = "SELECT idcerveza, idmarca, nombre, aspecto, procedimientos, graduacion, existencia_total FROM cerveza WHERE " + field + " = ?";
        List<Cerveza> list = new ArrayList<>();
        try (Connection cn = Database.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setObject(1, value);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Cerveza(
                        rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5),
                        rs.getObject(6) == null ? null : rs.getDouble(6), rs.getInt(7)
                ));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
    private List<Cerveza> findByFieldLike(String field, String value) {
        // Determinar si el campo es de texto o numérico
        boolean esCampoTexto = field.equals("nombre") || field.equals("aspecto") || field.equals("procedimientos");
        boolean esCampoNumerico = field.equals("idcerveza") || field.equals("idmarca") || field.equals("graduacion") || field.equals("existencia_total");
        List<Cerveza> list = new ArrayList<>();
        String sql;
        try (Connection cn = Database.getConnection()) {
            if (esCampoTexto) {
                sql = "SELECT idcerveza, idmarca, nombre, aspecto, procedimientos, graduacion, existencia_total FROM cerveza WHERE "+field+" LIKE ?";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setString(1, "%" + value + "%");
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        list.add(new Cerveza(
                                rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5),
                                rs.getObject(6) == null ? null : rs.getDouble(6), rs.getInt(7)
                        ));
                    }
                }
            } else if (esCampoNumerico && value.matches("\\d+(\\.\\d+)?")) {
                // Si el valor es numérico, buscar por igualdad
                sql = "SELECT idcerveza, idmarca, nombre, aspecto, procedimientos, graduacion, existencia_total FROM cerveza WHERE "+field+" = ?";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    if (field.equals("graduacion"))
                        ps.setDouble(1, Double.parseDouble(value));
                    else
                        ps.setInt(1, Integer.parseInt(value));
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        list.add(new Cerveza(
                                rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5),
                                rs.getObject(6) == null ? null : rs.getDouble(6), rs.getInt(7)
                        ));
                    }
                }
            } else {
                // Si no es numérico, buscar como texto
                sql = "SELECT idcerveza, idmarca, nombre, aspecto, procedimientos, graduacion, existencia_total FROM cerveza WHERE CAST("+field+" AS TEXT) LIKE ?";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setString(1, "%" + value + "%");
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        list.add(new Cerveza(
                                rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5),
                                rs.getObject(6) == null ? null : rs.getDouble(6), rs.getInt(7)
                        ));
                    }
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
}
