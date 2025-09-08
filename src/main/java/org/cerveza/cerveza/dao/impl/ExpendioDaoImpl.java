package org.cerveza.cerveza.dao.impl;

import org.cerveza.cerveza.dao.ExpendioDao;
import org.cerveza.cerveza.model.Expendio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.cerveza.cerveza.config.Database.getConnection;

public class ExpendioDaoImpl implements ExpendioDao {

    private static final String SELECT_BASE =
            "SELECT idexpendio, nombre, ubicacion, rfc, responsable FROM expendio ";

    @Override
    public Expendio insert(Expendio e) throws Exception {
        String sql = "INSERT INTO expendio (nombre, ubicacion, rfc, responsable) VALUES (?,?,?,?)";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, e.getNombre());
            ps.setString(2, e.getUbicacion());
            ps.setString(3, e.getRfc());
            ps.setString(4, e.getResponsable());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) e.setIdexpendio(rs.getInt(1));
            }
        }
        return e;
    }

    @Override
    public boolean update(Expendio e) throws Exception {
        String sql = "UPDATE expendio SET nombre=?, ubicacion=?, rfc=?, responsable=? WHERE idexpendio=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, e.getNombre());
            ps.setString(2, e.getUbicacion());
            ps.setString(3, e.getRfc());
            ps.setString(4, e.getResponsable());
            ps.setInt(5, e.getIdexpendio());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int idexpendio) throws Exception {
        String sql = "DELETE FROM expendio WHERE idexpendio=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idexpendio);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Expendio findById(int idexpendio) throws Exception {
        String sql = SELECT_BASE + "WHERE idexpendio=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idexpendio);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    @Override
    public List<Expendio> findAll() throws Exception {
        String sql = SELECT_BASE + "ORDER BY nombre ASC, idexpendio ASC";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Expendio> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        }
    }

    private Expendio map(ResultSet rs) throws SQLException {
        Expendio e = new Expendio();
        e.setIdexpendio(rs.getInt("idexpendio"));
        e.setNombre(rs.getString("nombre"));
        e.setUbicacion(rs.getString("ubicacion"));
        e.setRfc(rs.getString("rfc"));
        e.setResponsable(rs.getString("responsable"));
        return e;
    }
}
