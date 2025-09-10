package org.cerveza.cerveza.dao.impl;

import org.cerveza.cerveza.config.Database;
import org.cerveza.cerveza.dao.OrdenDao;
import org.cerveza.cerveza.model.Orden;

import java.sql.*;
import java.util.*;

public class OrdenDaoImpl implements OrdenDao {
    @Override
    public void insert(Orden orden) throws Exception {
        String sql = "INSERT INTO orden(idpresentacion, cantidad, fecha_orden, fecha_despacho) VALUES(?,?,?,?)";
        try (Connection cn = Database.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, orden.getIdPresentacion());
            ps.setInt(2, orden.getCantidad());
            ps.setDate(3, orden.getFecha_orden());
            ps.setDate(4, orden.getFecha_despacho());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Orden orden) throws Exception {
        String sql = "UPDATE orden SET idpresentacion=?, cantidad=?, fecha_orden=?, fecha_despacho=? WHERE idorden=?";
        try (Connection cn = Database.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, orden.getIdPresentacion());
            ps.setInt(2, orden.getCantidad());
            ps.setDate(3, orden.getFecha_orden());
            ps.setDate(4, orden.getFecha_despacho());
            ps.setInt(5, orden.getIdOrden());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM orden WHERE idorden=?";
        try (Connection cn = Database.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Orden> findAll() throws Exception {
        String sql = "SELECT idorden, idpresentacion, cantidad, fecha_orden, fecha_despacho FROM orden ORDER BY fecha_orden DESC";
        List<Orden> list = new ArrayList<>();
        try (Connection cn = Database.getConnection(); Statement st = cn.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                list.add(new Orden(
                    rs.getInt(1),
                    rs.getInt(2),
                    rs.getInt(3),
                    rs.getDate(4),
                    rs.getDate(5)
                ));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
}
