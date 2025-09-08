package org.cerveza.cerveza.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.cerveza.cerveza.dao.ExistenciaDao;
import org.cerveza.cerveza.dao.impl.ExistenciaDaoImpl;
import org.cerveza.cerveza.model.Existencia;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

import static org.cerveza.cerveza.config.Database.getConnection;

public class ExistenciaFormController {

    @FXML private ComboBox<IdName> cmbExpendio;
    @FXML private ComboBox<IdName> cmbPresentacion;
    @FXML private TextField txtCantidad;
    @FXML private DatePicker dpFecha;

    @FXML private TableView<Existencia> tblExistencias;
    @FXML private TableColumn<Existencia, Number> colId;
    @FXML private TableColumn<Existencia, String> colExpendio;
    @FXML private TableColumn<Existencia, String> colPresentacion;
    @FXML private TableColumn<Existencia, Number> colCantidad;
    @FXML private TableColumn<Existencia, String> colFecha;

    private final ExistenciaDao dao = new ExistenciaDaoImpl();
    private Existencia seleccionado;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIdExistencia()));
        colExpendio.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                Optional.ofNullable(c.getValue().getExpendioNombre()).orElse("id=" + c.getValue().getIdExpendio())));
        colPresentacion.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                Optional.ofNullable(c.getValue().getPresentacionNombre()).orElse("id=" + c.getValue().getIdPresentacion())));
        colCantidad.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getCantidad()));
        colFecha.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getFecha().toString()));

        cargarCombos();
        refrescarTabla();

        tblExistencias.getSelectionModel().selectedItemProperty().addListener((obs, a, b) -> {
            seleccionado = b;
            if (b != null) {
                seleccionarComboPorId(cmbExpendio, b.getIdExpendio());
                seleccionarComboPorId(cmbPresentacion, b.getIdPresentacion());
                txtCantidad.setText(Integer.toString(b.getCantidad()));
                dpFecha.setValue(b.getFecha());
            }
        });
    }

    private void cargarCombos() {
        cmbExpendio.setItems(cargarIdName("SELECT idexpendio, nombre FROM expendio ORDER BY nombre", "idexpendio", "nombre"));
        cmbPresentacion.setItems(cargarIdName("""
            SELECT p.idpresentacion,
                   CONCAT(e.nombre,' - ',c.nombre) AS nom
            FROM presentacion p
            JOIN envase e ON e.idenvase=p.idenvase
            JOIN cerveza c ON c.idcerveza=p.idcerveza
            ORDER BY nom
        """, "idpresentacion", "nom"));
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

    private void limpiar() {
        tblExistencias.getSelectionModel().clearSelection();
        cmbExpendio.getSelectionModel().clearSelection();
        cmbPresentacion.getSelectionModel().clearSelection();
        txtCantidad.clear();
        dpFecha.setValue(null);
        seleccionado = null;
    }

    private void refrescarTabla() {
        tblExistencias.setItems(FXCollections.observableArrayList(dao.findAllWithLabels()));
        limpiar();
    }

    @FXML
    private void onNuevo() { limpiar(); }

    @FXML
    private void onGuardar() {
        IdName exp = cmbExpendio.getValue();
        IdName pre = cmbPresentacion.getValue();
        String cantStr = txtCantidad.getText();
        LocalDate fecha = dpFecha.getValue();

        if (exp == null || pre == null || cantStr == null || cantStr.isBlank() || fecha == null) {
            new Alert(Alert.AlertType.WARNING, "Completa expendio, presentación, cantidad y fecha.").showAndWait();
            return;
        }
        int cantidad;
        try { cantidad = Integer.parseInt(cantStr); }
        catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.WARNING, "Cantidad debe ser numérica.").showAndWait(); return;
        }

        if (seleccionado == null) {
            Existencia e = new Existencia(0, exp.id(), pre.id(), cantidad, fecha, exp.name(), pre.name());
            int id = dao.insert(e);
            if (id > 0) refrescarTabla();
        } else {
            seleccionado.setIdExpendio(exp.id());
            seleccionado.setIdPresentacion(pre.id());
            seleccionado.setCantidad(cantidad);
            seleccionado.setFecha(fecha);
            if (dao.update(seleccionado)) refrescarTabla();
        }
    }

    @FXML
    private void onEliminar() {
        Existencia row = tblExistencias.getSelectionModel().getSelectedItem();
        if (row == null) return;
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar la existencia #" + row.getIdExistencia() + "?", ButtonType.OK, ButtonType.CANCEL);
        if (a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (dao.delete(row.getIdExistencia())) refrescarTabla();
        }
    }

    // record simple para combos
    public record IdName(int id, String name) { @Override public String toString(){ return name; } }
}
