package org.cerveza.cerveza.dao.impl;

import org.cerveza.cerveza.dao.ProduccionDao;
import org.cerveza.cerveza.config.Database;
import org.cerveza.cerveza.model.Produccion;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProduccionDaoImpl implements ProduccionDao {

    private static final String SELECT_ALL = """
        SELECT p.idproduccion,
               p.idcerveza,
               c.nombre AS cerveza_nombre,
               p.fecha,
               p.cantidad
          FROM produccion p
          JOIN cerveza c ON c.idcerveza = p.idcerveza
         ORDER BY p.idproduccion
        """;

    private static final String INSERT = """
        INSERT INTO produccion (idcerveza, fecha, cantidad)
        VALUES (?, ?, ?)
        """;

    private static final String UPDATE = """
        UPDATE produccion
           SET idcerveza = ?, fecha = ?, cantidad = ?
         WHERE idproduccion = ?
        """;

    private static final String DELETE = "DELETE FROM produccion WHERE id_produccion = ?";

    @Override
    public List<Produccion> findAll() {
        List<Produccion> list = new ArrayList<>();
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LocalDate fecha = rs.getDate("fecha").toLocalDate();
                Produccion p = new Produccion(
                        rs.getInt("id_produccion"),
                        rs.getInt("cerveza_id"),
                        rs.getString("cerveza_nombre"),
                        fecha,
                        rs.getInt("cantidad"),
                        rs.getString("lote")
                );
                list.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listando producciones", e);
        }
        return list;
    }

    @Override
    public void insert(Produccion p) {
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, p.getCervezaId());
            ps.setDate(2, Date.valueOf(p.getFecha()));
            ps.setInt(3, p.getCantidad());
            ps.setString(4, p.getLote());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error insertando producción", e);
        }
    }

    @Override
    public void update(Produccion p) {
        if (p.getId() == null) throw new IllegalArgumentException("ID requerido para actualizar");
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(UPDATE)) {

            ps.setInt(1, p.getCervezaId());
            ps.setDate(2, Date.valueOf(p.getFecha()));
            ps.setInt(3, p.getCantidad());
            ps.setString(4, p.getLote());
            ps.setInt(5, p.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando producción", e);
        }
    }

    @Override
    public void delete(int id) {
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(DELETE)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando producción", e);
        }
    }
}
