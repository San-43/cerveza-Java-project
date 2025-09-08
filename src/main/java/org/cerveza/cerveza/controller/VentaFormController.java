package org.cerveza.cerveza.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.cerveza.cerveza.dao.impl.VentaDaoImpl;
import org.cerveza.cerveza.model.Venta;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.time.LocalDate;

import static org.cerveza.cerveza.config.Database.getConnection;

public class VentaFormController {

    @FXML private ComboBox<ComboItem> cboExpendio;
    @FXML private ComboBox<ComboItem> cboPresentacion;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtCantidad;

    @FXML private TableView<Venta> tblVentas;
    @FXML private TableColumn<Venta, Integer> colId;
    @FXML private TableColumn<Venta, Integer> colExpendio;
    @FXML private TableColumn<Venta, Integer> colPresentacion;
    @FXML private TableColumn<Venta, LocalDate> colFecha;
    @FXML private TableColumn<Venta, Integer> colCantidad;

    private final VentaDaoImpl dao = new VentaDaoImpl();
    private Venta seleccionado;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idventa"));
        colExpendio.setCellValueFactory(new PropertyValueFactory<>("idexpendio"));
        colPresentacion.setCellValueFactory(new PropertyValueFactory<>("idpresentacion"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        cargarCombos();
        refrescarTabla();

        tblVentas.getSelectionModel().selectedItemProperty().addListener((obs, a, b) -> {
            seleccionado = b;
            if (b != null) {
                selectComboById(cboExpendio, b.getIdexpendio());
                selectComboById(cboPresentacion, b.getIdpresentacion());
                dpFecha.setValue(b.getFecha());
                txtCantidad.setText(String.valueOf(b.getCantidad()));
            }
        });
    }

    private void cargarCombos() {
        cboExpendio.setItems(FXCollections.observableArrayList(consultarIdNombre("expendio", "idexpendio")));
        // Para presentacion mostraremos "id - cerveza/envase"
        cboPresentacion.setItems(FXCollections.observableArrayList(consultarIdNombre("presentacion", "idpresentacion")));
    }

    private javafx.collections.ObservableList<ComboItem> consultarIdNombre(String tabla, String idCol) {
        var data = FXCollections.<ComboItem>observableArrayList();
        String sqlNombre = getString(tabla, idCol);
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sqlNombre);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) data.add(new ComboItem(rs.getInt("id"), rs.getString("nombre")));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    @NotNull
    private static String getString(String tabla, String idCol) {
        String etiqueta = "nombre";
        String sqlNombre = switch (tabla) {
            case "expendio" -> "SELECT idexpendio AS id, nombre FROM expendio ORDER BY nombre";
            // presentacion no tiene 'nombre'; mostramos un label armable "Presentación X"
            case "presentacion" -> "SELECT idpresentacion AS id, CONCAT('Presentación ', idpresentacion) AS nombre FROM presentacion ORDER BY idpresentacion";
            default -> "SELECT " + idCol + " AS id, " + etiqueta + " FROM " + tabla;
        };
        return sqlNombre;
    }

    private void selectComboById(ComboBox<ComboItem> combo, Integer id) {
        if (id == null) return;
        for (ComboItem item : combo.getItems()) if (item.id == id) { combo.getSelectionModel().select(item); return; }
    }

    private void refrescarTabla() {
        tblVentas.setItems(FXCollections.observableArrayList(dao.findAll()));
    }

    @FXML
    private void onNuevo() {
        tblVentas.getSelectionModel().clearSelection();
        seleccionado = null;
        cboExpendio.getSelectionModel().clearSelection();
        cboPresentacion.getSelectionModel().clearSelection();
        dpFecha.setValue(null);
        txtCantidad.clear();
    }

    @FXML
    private void onGuardar() {
        var expendio = cboExpendio.getSelectionModel().getSelectedItem();
        var present = cboPresentacion.getSelectionModel().getSelectedItem();
        var fecha = dpFecha.getValue();
        if (expendio == null || present == null || fecha == null || txtCantidad.getText().isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Completa expendio, presentación, fecha y cantidad").showAndWait();
            return;
        }
        int cantidad = Integer.parseInt(txtCantidad.getText());

        if (seleccionado == null) {
            dao.insert(new Venta(null, expendio.id, present.id, fecha, cantidad));
        } else {
            seleccionado.setIdexpendio(expendio.id);
            seleccionado.setIdpresentacion(present.id);
            seleccionado.setFecha(fecha);
            seleccionado.setCantidad(cantidad);
            dao.update(seleccionado);
        }
        refrescarTabla();
        onNuevo();
    }

    @FXML
    private void onEliminar() {
        Venta v = tblVentas.getSelectionModel().getSelectedItem();
        if (v == null) return;
        if (new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar venta " + v.getIdventa() + "?", ButtonType.OK, ButtonType.CANCEL)
                .showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            dao.delete(v.getIdventa());
            refrescarTabla();
            onNuevo();
        }
    }

    public static class ComboItem {
        public final int id; public final String nombre;
        public ComboItem(int id, String nombre) { this.id = id; this.nombre = nombre; }
        @Override public String toString() { return nombre + " (ID " + id + ")"; }
    }
}
