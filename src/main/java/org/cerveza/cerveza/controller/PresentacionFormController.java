package org.cerveza.cerveza.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.cerveza.cerveza.dao.PresentacionDao;
import org.cerveza.cerveza.dao.impl.PresentacionDaoImpl;
import org.cerveza.cerveza.model.Presentacion;

import java.sql.*;
import java.util.Optional;

import static org.cerveza.cerveza.config.Database.getConnection;

public class PresentacionFormController {

    @FXML private ComboBox<IdName> cmbEnvase;
    @FXML private ComboBox<IdName> cmbCerveza;
    @FXML private TableView<Presentacion> tblPresentaciones;
    @FXML private TableColumn<Presentacion, Number> colId;
    @FXML private TableColumn<Presentacion, String> colEnvase;
    @FXML private TableColumn<Presentacion, String> colCerveza;
    @FXML private Button btnNuevo, btnGuardar, btnEliminar;

    private final PresentacionDao dao = new PresentacionDaoImpl();
    private Presentacion seleccionado;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIdPresentacion()));
        colEnvase.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                Optional.ofNullable(c.getValue().getEnvaseNombre()).orElse("id=" + c.getValue().getIdEnvase())));
        colCerveza.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                Optional.ofNullable(c.getValue().getCervezaNombre()).orElse("id=" + c.getValue().getIdCerveza())));

        cargarCombos();
        refrescarTabla();

        tblPresentaciones.getSelectionModel().selectedItemProperty().addListener((obs, a, b) -> {
            seleccionado = b;
            if (b != null) {
                seleccionarComboPorId(cmbEnvase, b.getIdEnvase());
                seleccionarComboPorId(cmbCerveza, b.getIdCerveza());
            }
        });
    }

    private void cargarCombos() {
        cmbEnvase.setItems(cargarIdName("SELECT idenvase, nombre FROM envase ORDER BY nombre", "idenvase", "nombre"));
        cmbCerveza.setItems(cargarIdName("SELECT idcerveza, nombre FROM cerveza ORDER BY nombre", "idcerveza", "nombre"));
    }

    private ObservableList<IdName> cargarIdName(String sql, String idCol, String nameCol) {
        ObservableList<IdName> data = FXCollections.observableArrayList();
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) data.add(new IdName(rs.getInt(idCol), rs.getString(nameCol)));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    private void seleccionarComboPorId(ComboBox<IdName> combo, int id) {
        for (IdName i : combo.getItems()) if (i.id() == id) { combo.getSelectionModel().select(i); break; }
    }

    private void refrescarTabla() {
        tblPresentaciones.setItems(FXCollections.observableArrayList(dao.findAllWithLabels()));
        tblPresentaciones.getSelectionModel().clearSelection();
        seleccionado = null;
    }

    @FXML
    private void onNuevo() {
        tblPresentaciones.getSelectionModel().clearSelection();
        cmbEnvase.getSelectionModel().clearSelection();
        cmbCerveza.getSelectionModel().clearSelection();
        seleccionado = null;
    }

    @FXML
    private void onGuardar() {
        IdName env = cmbEnvase.getValue();
        IdName cz = cmbCerveza.getValue();
        if (env == null || cz == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona envase y cerveza.").showAndWait();
            return;
        }

        if (seleccionado == null) {
            Presentacion p = new Presentacion(0, env.id(), cz.id(), env.name(), cz.name());
            int id = dao.insert(p);
            if (id > 0) refrescarTabla();
        } else {
            seleccionado.setIdEnvase(env.id());
            seleccionado.setIdCerveza(cz.id());
            if (dao.update(seleccionado)) refrescarTabla();
        }
    }

    @FXML
    private void onEliminar() {
        Presentacion row = tblPresentaciones.getSelectionModel().getSelectedItem();
        if (row == null) return;
        if (confirm("¿Eliminar la presentación #" + row.getIdPresentacion() + "?")) {
            if (dao.delete(row.getIdPresentacion())) refrescarTabla();
        }
    }

    private boolean confirm(String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL);
        return a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    // pequeño record para combos
    public record IdName(int id, String name) { @Override public String toString(){ return name; } }
}
