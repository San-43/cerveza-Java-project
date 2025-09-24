package org.cerveza.cerveza.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.cerveza.cerveza.dao.impl.RecetaDaoImpl;
import org.cerveza.cerveza.model.Receta;

import java.sql.*;

import static org.cerveza.cerveza.config.Database.getConnection;

public class RecetaFormController {

    @FXML private ComboBox<ComboItem> cboCerveza;
    @FXML private ComboBox<ComboItem> cboIngrediente;
    @FXML private TextField txtCantidad;

    @FXML private TableView<Receta> tblRecetas;
    @FXML private TableColumn<Receta, Integer> colId;
    @FXML private TableColumn<Receta, Integer> colCerveza;
    @FXML private TableColumn<Receta, Integer> colIngrediente;
    @FXML private TableColumn<Receta, Integer> colCantidad;

    private final RecetaDaoImpl dao = new RecetaDaoImpl();
    private Receta seleccionado;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idreceta"));
        colCerveza.setCellValueFactory(new PropertyValueFactory<>("idcerveza"));
        colIngrediente.setCellValueFactory(new PropertyValueFactory<>("idingrediente"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        cargarCombos();
        refrescarTabla();

        tblRecetas.getSelectionModel().selectedItemProperty().addListener((obs, a, b) -> {
            seleccionado = b;
            if (b != null) {
                selectComboById(cboCerveza, b.getIdcerveza());
                selectComboById(cboIngrediente, b.getIdingrediente());
                txtCantidad.setText(b.getCantidad() == null ? "" : String.valueOf(b.getCantidad()));
            }
        });
    }

    private void cargarCombos() {
        cboCerveza.setItems(FXCollections.observableArrayList(consultarIdNombre("cerveza")));
        cboIngrediente.setItems(FXCollections.observableArrayList(consultarIdNombre("ingrediente")));
    }

    private ObservableList<ComboItem> consultarIdNombre(String tabla) {
        ObservableList<ComboItem> data = FXCollections.observableArrayList();
        String sql = "SELECT " + (tabla.equals("cerveza") ? "idcerveza" : "idingrediente") + " AS id, nombre FROM " + tabla + " ORDER BY nombre";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) data.add(new ComboItem(rs.getInt("id"), rs.getString("nombre")));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    private void selectComboById(ComboBox<ComboItem> combo, Integer id) {
        if (id == null) return;
        for (ComboItem item : combo.getItems()) {
            if (item.id == id) { combo.getSelectionModel().select(item); return; }
        }
    }

    private void refrescarTabla() {
        tblRecetas.setItems(FXCollections.observableArrayList(dao.findAll()));
    }

    @FXML
    private void onNuevo() {
        tblRecetas.getSelectionModel().clearSelection();
        seleccionado = null;
        cboCerveza.getSelectionModel().clearSelection();
        cboIngrediente.getSelectionModel().clearSelection();
        txtCantidad.clear();
    }

    @FXML
    private void onGuardar() {
        ComboItem cerveza = cboCerveza.getSelectionModel().getSelectedItem();
        ComboItem ing = cboIngrediente.getSelectionModel().getSelectedItem();
        if (cerveza == null || ing == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona cerveza e ingrediente").showAndWait();
            return;
        }
        Integer cantidad = txtCantidad.getText().isBlank() ? null : Integer.parseInt(txtCantidad.getText());

        if (seleccionado == null) {
            Receta r = new Receta(null, cerveza.id, ing.id, cantidad);
            dao.insert(r);
        } else {
            seleccionado.setIdcerveza(cerveza.id);
            seleccionado.setIdingrediente(ing.id);
            seleccionado.setCantidad(cantidad);
            dao.update(seleccionado);
        }
        refrescarTabla();
        onNuevo();
    }

    @FXML
    private void onEliminar() {
        Receta r = tblRecetas.getSelectionModel().getSelectedItem();
        if (r == null) return;
        if (new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar receta " + r.getIdreceta() + "?", ButtonType.OK, ButtonType.CANCEL).showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            dao.delete(r.getIdreceta());
            refrescarTabla();
            onNuevo();
        }
    }

    @FXML
    private void onListar() {
        refrescarTabla();
        onNuevo();
    }

    // Item simple para combos
        public record ComboItem(int id, String nombre) {
        @Override
        public String toString() {
            return nombre + " (ID " + id + ")";
        }
        }
}
