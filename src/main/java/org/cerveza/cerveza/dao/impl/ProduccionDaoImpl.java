package org.cerveza.cerveza.dao.impl;

import org.cerveza.cerveza.dao.ProduccionDao;
import org.cerveza.cerveza.model.Produccion;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.cerveza.cerveza.config.Database.*;

public class ProduccionDaoImpl implements ProduccionDao {

    private static final String SELECT_BASE =
            "SELECT p.idproduccion, p.idcerveza, p.fecha, p.cantidad, c.nombre AS cerveza_nombre " +
                    "FROM produccion p JOIN cerveza c ON c.idcerveza = p.idcerveza ";

    @Override
    public Produccion insert(Produccion p) throws Exception {
        String sql = "INSERT INTO produccion (idcerveza, fecha, cantidad) VALUES (?,?,?)";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, p.getCervezaId());
            ps.setDate(2, Date.valueOf(p.getFecha()));
            ps.setInt(3, p.getCantidad());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) p.setId(rs.getInt(1));
            }
        }
        return p;
    }

    @Override
    public boolean update(Produccion p) throws Exception {
        String sql = "UPDATE produccion SET idcerveza=?, fecha=?, cantidad=? WHERE idproduccion=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, p.getCervezaId());
            ps.setDate(2, Date.valueOf(p.getFecha()));
            ps.setInt(3, p.getCantidad());
            ps.setInt(4, p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws Exception {
        String sql = "DELETE FROM produccion WHERE idproduccion=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Produccion findById(int id) throws Exception {
        String sql = SELECT_BASE + "WHERE p.idproduccion=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    @Override
    public List<Produccion> findAll() throws Exception {
        String sql = SELECT_BASE + "ORDER BY p.fecha DESC, p.idproduccion DESC";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Produccion> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        }
    }

    @Override
    public List<Produccion> findByCerveza(int cervezaId) throws Exception {
        String sql = SELECT_BASE + "WHERE p.idcerveza=? ORDER BY p.fecha DESC";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, cervezaId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Produccion> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        }
    }

    private Produccion map(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("idproduccion");
        Integer cervezaId = rs.getInt("idcerveza");
        LocalDate fecha = rs.getDate("fecha").toLocalDate();
        Integer cantidad = rs.getInt("cantidad");
        String cervezaNombre = rs.getString("cerveza_nombre");
        return new Produccion(id, cervezaId, fecha, cantidad, cervezaNombre);
    }
}
