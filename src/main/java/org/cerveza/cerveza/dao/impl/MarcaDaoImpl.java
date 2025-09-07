package org.cerveza.cerveza.dao.impl;

import org.cerveza.cerveza.config.Database;
import org.cerveza.cerveza.dao.MarcaDao;
import org.cerveza.cerveza.model.Marca;

import java.sql.*;
import java.util.*;

public class MarcaDaoImpl implements MarcaDao {
    @Override
    public void insert(Marca m) {
        String sql = "INSERT INTO marca(idfabricante, nombre, descripcion) VALUES(?,?,?)";
        try (Connection cn = Database.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, m.getIdFabricante());
            ps.setString(2, m.getNombre());
            ps.setString(3, m.getDescripcion());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Marca m) {
        String sql = "UPDATE marca SET idfabricante=?, nombre=?, descripcion=? WHERE idmarca=?";
        try (Connection cn = Database.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, m.getIdFabricante());
            ps.setString(2, m.getNombre());
            ps.setString(3, m.getDescripcion());
            ps.setInt(4, m.getId());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM marca WHERE idmarca=?";
        try (Connection cn = Database.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<Marca> findById(int id) {
        String sql = "SELECT idmarca, idfabricante, nombre, descripcion FROM marca WHERE idmarca=?";
        try (Connection cn = Database.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Marca m = new Marca(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4));
                return Optional.of(m);
            }
            return Optional.empty();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Marca> findAll() {
        String sql = "SELECT idmarca, idfabricante, nombre, descripcion FROM marca ORDER BY idmarca DESC";
        List<Marca> list = new ArrayList<>();
        try (Connection cn = Database.getConnection(); Statement st = cn.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                list.add(new Marca(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4)));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
}
