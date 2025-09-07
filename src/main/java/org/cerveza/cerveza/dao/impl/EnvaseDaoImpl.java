package org.cerveza.cerveza.dao.impl;

import org.cerveza.cerveza.config.Database;
import org.cerveza.cerveza.dao.EnvaseDao;
import org.cerveza.cerveza.model.Envase;

import java.sql.*;
import java.util.*;

public class EnvaseDaoImpl implements EnvaseDao {
    @Override
    public void insert(Envase e) {
        String sql = "INSERT INTO envase(nombre, material, capacidad, descripcion) VALUES(?,?,?,?)";
        try (Connection cn = Database.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getMaterial());
            ps.setString(3, e.getCapacidad());
            ps.setString(4, e.getDescripcion());
            ps.executeUpdate();
        } catch (SQLException ex) { throw new RuntimeException(ex); }
    }

    @Override
    public void update(Envase e) {
        String sql = "UPDATE envase SET nombre=?, material=?, capacidad=?, descripcion=? WHERE idenvase=?";
        try (Connection cn = Database.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getMaterial());
            ps.setString(3, e.getCapacidad());
            ps.setString(4, e.getDescripcion());
            ps.setInt(5, e.getId());
            ps.executeUpdate();
        } catch (SQLException ex) { throw new RuntimeException(ex); }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM envase WHERE idenvase=?";
        try (Connection cn = Database.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) { throw new RuntimeException(ex); }
    }

    @Override
    public Optional<Envase> findById(int id) {
        String sql = "SELECT idenvase, nombre, material, capacidad, descripcion FROM envase WHERE idenvase=?";
        try (Connection cn = Database.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Envase e = new Envase(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
                return Optional.of(e);
            }
            return Optional.empty();
        } catch (SQLException ex) { throw new RuntimeException(ex); }
    }

    @Override
    public List<Envase> findAll() {
        String sql = "SELECT idenvase, nombre, material, capacidad, descripcion FROM envase ORDER BY idenvase DESC";
        List<Envase> list = new ArrayList<>();
        try (Connection cn = Database.getConnection(); Statement st = cn.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                list.add(new Envase(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)));
            }
        } catch (SQLException ex) { throw new RuntimeException(ex); }
        return list;
    }
}
