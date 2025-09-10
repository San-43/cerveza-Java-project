package org.cerveza.cerveza.dao.impl;

import org.cerveza.cerveza.dao.ExistenciaDao;
import org.cerveza.cerveza.model.Existencia;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.cerveza.cerveza.config.Database.getConnection;

public class ExistenciaDaoImpl implements ExistenciaDao {

    @Override
    public List<Existencia> findAll() {
        String sql = "SELECT idexistencia, idexpendio, idpresentacion, cantidad, fecha FROM existencia ORDER BY idexistencia";
        List<Existencia> list = new ArrayList<>();
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<Existencia> findAllWithLabels() {
        String sql = """
            SELECT e.idexistencia, e.idexpendio, ex.nombre AS expendio_nombre,
                   e.idpresentacion,
                   CONCAT(ev.nombre, ' - ', cz.nombre) AS presentacion_nombre,
                   e.cantidad, e.fecha
            FROM existencia e
            JOIN expendio ex ON ex.idexpendio = e.idexpendio
            JOIN presentacion p ON p.idpresentacion = e.idpresentacion
            JOIN envase ev ON ev.idenvase = p.idenvase
            JOIN cerveza cz ON cz.idcerveza = p.idcerveza
            ORDER BY e.idexistencia
        """;
        List<Existencia> list = new ArrayList<>();
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Existencia(
                        rs.getInt("idexistencia"),
                        rs.getInt("idexpendio"),
                        rs.getInt("idpresentacion"),
                        rs.getInt("cantidad"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getString("expendio_nombre"),
                        rs.getString("presentacion_nombre")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public Optional<Existencia> findById(int id) {
        String sql = "SELECT idexistencia, idexpendio, idpresentacion, cantidad, fecha FROM existencia WHERE idexistencia=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public int insert(Existencia e) {
        String sql = "INSERT INTO existencia (idexpendio, idpresentacion, cantidad, fecha) VALUES (?,?,?,?)";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, e.getIdExpendio());
            ps.setInt(2, e.getIdPresentacion());
            ps.setInt(3, e.getCantidad());
            ps.setDate(4, Date.valueOf(e.getFecha()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return 0;
    }

    @Override
    public boolean update(Existencia e) {
        String sql = "UPDATE existencia SET idexpendio=?, idpresentacion=?, cantidad=?, fecha=? WHERE idexistencia=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, e.getIdExpendio());
            ps.setInt(2, e.getIdPresentacion());
            ps.setInt(3, e.getCantidad());
            ps.setDate(4, Date.valueOf(e.getFecha()));
            ps.setInt(5, e.getIdExistencia());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM existencia WHERE idexistencia=?";   // ✅
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }


    private Existencia map(ResultSet rs) throws SQLException {
        int id = rs.getInt("idexistencia");
        int idExpendio = rs.getInt("idexpendio");
        int idPresentacion = rs.getInt("idpresentacion");
        int cantidad = rs.getInt("cantidad");
        LocalDate fecha = rs.getDate("fecha").toLocalDate();
        String expendio = getIfPresent(rs, "expendio_nombre");
        String presentacion = getIfPresent(rs, "presentacion_nombre");
        return new Existencia(id, idExpendio, idPresentacion, cantidad, fecha, expendio, presentacion);
    }

    private static String getIfPresent(ResultSet rs, String col) {
        try {
            rs.findColumn(col);      // lanza excepción si no existe
            return rs.getString(col);
        } catch (SQLException ignore) {
            return null;
        }
    }

}
