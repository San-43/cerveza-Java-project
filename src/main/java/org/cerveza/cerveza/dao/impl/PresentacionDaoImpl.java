package org.cerveza.cerveza.dao.impl;

import org.cerveza.cerveza.dao.PresentacionDao;
import org.cerveza.cerveza.model.Presentacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.cerveza.cerveza.config.Database.getConnection;

public class PresentacionDaoImpl implements PresentacionDao {

    @Override
    public List<Presentacion> findAll() {
        String sql = "SELECT idpresentacion, idenvase, idcerveza FROM presentacion ORDER BY idpresentacion";
        List<Presentacion> list = new ArrayList<>();
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Presentacion(
                        rs.getInt("idpresentacion"),
                        rs.getInt("idenvase"),
                        rs.getInt("idcerveza"),
                        null, null
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<Presentacion> findAllWithLabels() {
        String sql = """
            SELECT p.idpresentacion, p.idenvase, e.nombre AS envase_nombre,
                   p.idcerveza, c.nombre AS cerveza_nombre
            FROM presentacion p
            JOIN envase e ON e.idenvase = p.idenvase
            JOIN cerveza c ON c.idcerveza = p.idcerveza
            ORDER BY p.idpresentacion
        """;
        List<Presentacion> list = new ArrayList<>();
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Presentacion(
                        rs.getInt("idpresentacion"),
                        rs.getInt("idenvase"),
                        rs.getInt("idcerveza"),
                        rs.getString("envase_nombre"),
                        rs.getString("cerveza_nombre")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public Optional<Presentacion> findById(int id) {
        String sql = "SELECT idpresentacion, idenvase, idcerveza FROM presentacion WHERE idpresentacion=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Presentacion(
                            rs.getInt(1), rs.getInt(2), rs.getInt(3), null, null));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public int insert(Presentacion p) {
        String sql = "INSERT INTO presentacion (idenvase, idcerveza) VALUES (?, ?)";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getIdEnvase());
            ps.setInt(2, p.getIdCerveza());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    @Override
    public boolean update(Presentacion p) {
        String sql = "UPDATE presentacion SET idenvase=?, idcerveza=? WHERE idpresentacion=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, p.getIdEnvase());
            ps.setInt(2, p.getIdCerveza());
            ps.setInt(3, p.getIdPresentacion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM presentacion WHERE idpresentacion=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}
