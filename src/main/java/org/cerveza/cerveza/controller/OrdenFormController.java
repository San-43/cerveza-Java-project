package org.cerveza.cerveza.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.cerveza.cerveza.dao.OrdenDao;
import org.cerveza.cerveza.dao.impl.OrdenDaoImpl;
import org.cerveza.cerveza.model.Orden;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.sql.Date;

public class OrdenFormController {
    @FXML private TextField txtIdOrden, txtIdPresentacion, txtCantidad, txtFechaOrden, txtFechaDespacho;
    @FXML private Button btnGuardar, btnEliminar;
    @FXML private TableView<Orden> tblOrdenes;
    @FXML private TableColumn<Orden, Integer> colIdOrden, colIdPresentacion, colCantidad;
    @FXML private TableColumn<Orden, Date> colFechaOrden, colFechaDespacho;

    private final OrdenDao dao = new OrdenDaoImpl();
    private final ValidationSupport vs = new ValidationSupport();

    @FXML
    public void initialize() {
        // Table setup
        colIdOrden.setCellValueFactory(new PropertyValueFactory<>("idOrden"));
        colIdPresentacion.setCellValueFactory(new PropertyValueFactory<>("idPresentacion"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colFechaOrden.setCellValueFactory(new PropertyValueFactory<>("fecha_orden"));
        colFechaDespacho.setCellValueFactory(new PropertyValueFactory<>("fecha_despacho"));
        refreshTable();

        // Table selection listener to fill form
        tblOrdenes.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) fillForm(newSel);
        });

        // Validations
        vs.registerValidator(txtIdPresentacion, true, Validator.createRegexValidator("ID Presentación numérico", "^\\d+$", Severity.ERROR));
        vs.registerValidator(txtCantidad, true, Validator.createRegexValidator("Cantidad numérica", "^\\d+$", Severity.ERROR));
        vs.registerValidator(txtFechaOrden, true, Validator.createRegexValidator("Fecha orden formato YYYY-MM-DD", "^\\d{4}-\\d{2}-\\d{2}$", Severity.ERROR));
        vs.registerValidator(txtFechaDespacho, false, Validator.createRegexValidator("Fecha despacho formato YYYY-MM-DD", "^\\d{4}-\\d{2}-\\d{2}$", Severity.ERROR));

        btnGuardar.disableProperty().bind(Bindings.createBooleanBinding(() -> vs.isInvalid(), vs.invalidProperty()));
    }

    private void fillForm(Orden orden) {
        txtIdOrden.setText(orden.getIdOrden() != null ? orden.getIdOrden().toString() : "");
        txtIdPresentacion.setText(orden.getIdPresentacion() != null ? orden.getIdPresentacion().toString() : "");
        txtCantidad.setText(orden.getCantidad() != null ? orden.getCantidad().toString() : "");
        txtFechaOrden.setText(orden.getFecha_orden() != null ? orden.getFecha_orden().toString() : "");
        txtFechaDespacho.setText(orden.getFecha_despacho() != null ? orden.getFecha_despacho().toString() : "");
    }

    @FXML
    public void onNuevo() { onLimpiar(); }

    @FXML
    public void onGuardar() {
        try {
            Integer idPresentacion = Integer.parseInt(txtIdPresentacion.getText().trim());
            Integer cantidad = Integer.parseInt(txtCantidad.getText().trim());
            Date fechaOrden = Date.valueOf(txtFechaOrden.getText().trim());
            Date fechaDespacho = txtFechaDespacho.getText().isBlank() ? null : Date.valueOf(txtFechaDespacho.getText().trim());

            Orden orden = new Orden(idPresentacion, cantidad, fechaOrden, fechaDespacho);
            dao.insert(orden);
            showInfo("Guardado", "Orden registrada correctamente.");
            refreshTable();
            onLimpiar();
        } catch (NumberFormatException ex) {
            showError("Revisa los campos numéricos.");
        } catch (IllegalArgumentException ex) {
            showError("Revisa los campos de fecha (YYYY-MM-DD).");
        } catch (Exception ex) {
            showError("Error al guardar: " + ex.getMessage());
        }
    }

    @FXML
    public void onEliminar() {
        Orden selected = tblOrdenes.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Selecciona una orden para eliminar.");
            return;
        }
        try {
            dao.delete(selected.getIdOrden());
            showInfo("Eliminado", "Orden eliminada correctamente.");
            refreshTable();
            onLimpiar();
        } catch (Exception ex) {
            showError("Error al eliminar: " + ex.getMessage());
        }
    }

    @FXML
    public void onLimpiar() {
        txtIdOrden.clear();
        txtIdPresentacion.clear();
        txtCantidad.clear();
        txtFechaOrden.clear();
        txtFechaDespacho.clear();
    }

    private void refreshTable() {
        try {
            tblOrdenes.setItems(FXCollections.observableArrayList(dao.findAll()));
        } catch (Exception ex) {
            showError("Error al cargar órdenes: " + ex.getMessage());
        }
    }

    private void showInfo(String header, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(header); a.setContentText(msg); a.showAndWait();
    }
    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText("Validación"); a.setContentText(msg); a.showAndWait();
    }
}
