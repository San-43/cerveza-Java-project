package org.cerveza.cerveza.dao.impl;

import org.cerveza.cerveza.dao.FabricanteDao;
import org.cerveza.cerveza.model.Fabricante;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FabricanteDaoImpl implements FabricanteDao {
    private final String url = "jdbc:mysql://localhost:3306/mydb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private final String user = "root";
    private final String password = "root";

    @Override
    public void insertar(Fabricante fabricante) {
        String sql = "INSERT INTO fabricante(nombre, pais, descripcion) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fabricante.getNombre());
            stmt.setString(2, fabricante.getPais());
            stmt.setString(3, fabricante.getDescripcion());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actualizar(Fabricante fabricante) {
        String sql = "UPDATE fabricante SET nombre=?, pais=?, descripcion=? WHERE idfabricante=?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fabricante.getNombre());
            stmt.setString(2, fabricante.getPais());
            stmt.setString(3, fabricante.getDescripcion());
            stmt.setInt(4, fabricante.getIdFabricante());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(int idFabricante) {
        String sql = "DELETE FROM fabricante WHERE idfabricante=?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idFabricante);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Fabricante obtenerPorId(int idFabricante) {
        String sql = "SELECT idfabricante, nombre, pais, descripcion FROM fabricante WHERE idfabricante=?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idFabricante);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Fabricante(
                    rs.getInt("idfabricante"),
                    rs.getString("nombre"),
                    rs.getString("pais"),
                    rs.getString("descripcion")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Fabricante> obtenerTodos() {
        List<Fabricante> lista = new ArrayList<>();
        String sql = "SELECT idfabricante, nombre, pais, descripcion FROM fabricante ORDER BY idfabricante DESC";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Fabricante(
                    rs.getInt("idfabricante"),
                    rs.getString("nombre"),
                    rs.getString("pais"),
                    rs.getString("descripcion")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
