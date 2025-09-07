package org.cerveza.cerveza.dao.impl;

import org.cerveza.cerveza.config.Database;
import org.cerveza.cerveza.dao.CervezaDao;
import org.cerveza.cerveza.model.Cerveza;

import java.sql.*;
import java.util.*;


public class CervezaDaoImpl implements CervezaDao {
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
        String sql = "SELECT idcerveza, idmarca, nombre, aspecto, procedimientos, graduacion FROM cerveza WHERE idcerveza=?";
        try (Connection cn = Database.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Cerveza c = new Cerveza(
                        rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5),
                        rs.getObject(6) == null ? null : rs.getDouble(6)
                );
                return Optional.of(c);
            }
            return Optional.empty();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }


    @Override
    public List<Cerveza> findAll() {
        String sql = "SELECT idcerveza, idmarca, nombre, aspecto, procedimientos, graduacion FROM cerveza ORDER BY idcerveza DESC";
        List<Cerveza> list = new ArrayList<>();
        try (Connection cn = Database.getConnection(); Statement st = cn.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                list.add(new Cerveza(
                        rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5),
                        rs.getObject(6) == null ? null : rs.getDouble(6)
                ));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
}
