package org.cerveza.cerveza.dao.impl;

import org.cerveza.cerveza.config.Database;
import org.cerveza.cerveza.dao.IngredienteDao;
import org.cerveza.cerveza.model.Expendio;
import org.cerveza.cerveza.model.Ingrediente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.cerveza.cerveza.config.Database.getConnection;

public class IngredienteDaoImpl implements IngredienteDao {
    private static final String SELECT_BASE =
            "SELECT idingrediente, nombre, descripcion FROM ingrediente ";

    @Override
    public void insert(Ingrediente ingrediente) {
        String sql = "INSERT INTO ingrediente (nombre, descripcion) VALUES (?, ?)";
        try (Connection cn = Database.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, ingrediente.getNombre());
            ps.setString(2, ingrediente.getDescripcion());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean update(Ingrediente ingrediente) throws Exception {
        String sql = "UPDATE ingrediente SET nombre = ?, descripcion = ? WHERE idingrediente = ?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, ingrediente.getNombre());
            ps.setString(2, ingrediente.getDescripcion());
            ps.setInt(3, ingrediente.getIdIngrediente());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws Exception{
        String sql = "DELETE FROM ingrediente WHERE idingrediente=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Ingrediente> findAll() throws Exception {
        String sql = SELECT_BASE + "ORDER BY nombre ASC, idingrediente ASC";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Ingrediente> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        }
    }

    private Ingrediente map(ResultSet rs) throws SQLException {
        Ingrediente e = new Ingrediente();
        e.setIdIngrediente(rs.getInt("idingrediente"));
        e.setNombre(rs.getString("nombre"));
        e.setDescripcion(rs.getString("descripcion"));
        return e;
    }
}
