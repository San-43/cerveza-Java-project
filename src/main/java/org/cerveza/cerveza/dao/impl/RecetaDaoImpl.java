package org.cerveza.cerveza.dao.impl;

import org.cerveza.cerveza.dao.RecetaDao;
import org.cerveza.cerveza.model.Receta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.cerveza.cerveza.config.Database.getConnection;

public class RecetaDaoImpl implements RecetaDao {


    @Override
    public List<Receta> findAll() {
        String sql = "SELECT idreceta, idcerveza, idingrediente, cantidad FROM receta";
        List<Receta> list = new ArrayList<>();
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Receta(
                        rs.getInt("idreceta"),
                        rs.getInt("idcerveza"),
                        rs.getInt("idingrediente"),
                        rs.getInt("cantidad")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public Optional<Receta> findById(int idreceta) {
        String sql = "SELECT idreceta, idcerveza, idingrediente, cantidad FROM receta WHERE idreceta=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idreceta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Receta(
                            rs.getInt("idreceta"),
                            rs.getInt("idcerveza"),
                            rs.getInt("idingrediente"),
                            rs.getInt("cantidad")
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public Receta insert(Receta r) {
        String sql = "INSERT INTO receta (idcerveza, idingrediente, cantidad) VALUES (?,?,?)";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getIdcerveza());
            ps.setInt(2, r.getIdingrediente());
            if (r.getCantidad() == null) ps.setNull(3, Types.INTEGER); else ps.setInt(3, r.getCantidad());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) r.setIdreceta(keys.getInt(1));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return r;
    }

    @Override
    public boolean update(Receta r) {
        String sql = "UPDATE receta SET idcerveza=?, idingrediente=?, cantidad=? WHERE idreceta=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, r.getIdcerveza());
            ps.setInt(2, r.getIdingrediente());
            if (r.getCantidad() == null) ps.setNull(3, Types.INTEGER); else ps.setInt(3, r.getCantidad());
            ps.setInt(4, r.getIdreceta());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean delete(int idreceta) {
        String sql = "DELETE FROM receta WHERE idreceta=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idreceta);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}
