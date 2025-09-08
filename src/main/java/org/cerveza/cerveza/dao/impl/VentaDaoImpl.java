package org.cerveza.cerveza.dao.impl;

import org.cerveza.cerveza.dao.VentaDao;
import org.cerveza.cerveza.model.Venta;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VentaDaoImpl implements VentaDao {

    private static final String URL  = "jdbc:mysql://localhost:3306/mydb?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "root";

    private Connection getConn() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    @Override
    public List<Venta> findAll() {
        String sql = "SELECT idventa, idexpendio, idpresentacion, fecha, cantidad FROM venta ORDER BY fecha DESC, idventa DESC";
        List<Venta> list = new ArrayList<>();
        try (Connection cn = getConn();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public Optional<Venta> findById(int idventa) {
        String sql = "SELECT idventa, idexpendio, idpresentacion, fecha, cantidad FROM venta WHERE idventa=?";
        try (Connection cn = getConn();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idventa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public Venta insert(Venta v) {
        String sql = "INSERT INTO venta (idexpendio, idpresentacion, fecha, cantidad) VALUES (?,?,?,?)";
        try (Connection cn = getConn();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, v.getIdexpendio());
            ps.setInt(2, v.getIdpresentacion());
            ps.setDate(3, Date.valueOf(v.getFecha()));
            ps.setInt(4, v.getCantidad());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) v.setIdventa(keys.getInt(1));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return v;
    }

    @Override
    public boolean update(Venta v) {
        String sql = "UPDATE venta SET idexpendio=?, idpresentacion=?, fecha=?, cantidad=? WHERE idventa=?";
        try (Connection cn = getConn();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, v.getIdexpendio());
            ps.setInt(2, v.getIdpresentacion());
            ps.setDate(3, Date.valueOf(v.getFecha()));
            ps.setInt(4, v.getCantidad());
            ps.setInt(5, v.getIdventa());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean delete(int idventa) {
        String sql = "DELETE FROM venta WHERE idventa=?";
        try (Connection cn = getConn();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idventa);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private Venta map(ResultSet rs) throws SQLException {
        return new Venta(
                rs.getInt("idventa"),
                rs.getInt("idexpendio"),
                rs.getInt("idpresentacion"),
                rs.getDate("fecha").toLocalDate(),
                rs.getInt("cantidad")
        );
    }
}
