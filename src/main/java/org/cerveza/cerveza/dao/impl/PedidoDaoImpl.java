package org.cerveza.cerveza.dao.impl;

import org.cerveza.cerveza.dao.PedidoDao;
import org.cerveza.cerveza.model.Pedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.cerveza.cerveza.config.Database.getConnection;

public class PedidoDaoImpl implements PedidoDao {
    @Override
    public List<Pedido> findAll() {
        String sql = "SELECT idpedido, idexpendio FROM pedido ORDER BY idpedido";
        List<Pedido> list = new ArrayList<>();
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Pedido(
                        rs.getInt("idpedido"),
                        rs.getInt("idexpendio")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public Optional<Pedido> findById(int id) {
        String sql = "SELECT idpedido, idexpendio FROM pedido WHERE idpedido=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Pedido(
                            rs.getInt("idpedido"),
                            rs.getInt("idexpendio")
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public Pedido insert(Pedido p) {
        String sql = "INSERT INTO pedido (idexpendio) VALUES (?)";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getIdexpendio());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    p.setIdpedido(keys.getInt(1));
                    return p;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public boolean update(Pedido p) {
        String sql = "UPDATE pedido SET idexpendio=? WHERE idpedido=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, p.getIdexpendio());
            ps.setInt(2, p.getIdpedido());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM pedido WHERE idpedido=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}

