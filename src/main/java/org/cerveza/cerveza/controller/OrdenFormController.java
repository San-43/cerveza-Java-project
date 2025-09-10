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
import org.controlsfx.validation.ValidationResult;

import java.sql.Date;
import java.time.LocalDate;

public class OrdenFormController {
    @FXML private TextField txtIdOrden, txtIdPresentacion, txtCantidad;
    @FXML private Button btnGuardar, btnEliminar;
    @FXML private TableView<Orden> tblOrdenes;
    @FXML private TableColumn<Orden, Integer> colIdOrden, colIdPresentacion, colCantidad;
    @FXML private TableColumn<Orden, Date> colFechaOrden, colFechaDespacho;
    @FXML private DatePicker dpFechaOrden, dpFechaDespacho;

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
        vs.registerValidator(dpFechaOrden, true, (c, value) -> ValidationResult.fromErrorIf(dpFechaOrden, "Fecha orden requerida", dpFechaOrden.getValue() == null));
        // Fecha despacho opcional, no requiere validación estricta
        btnGuardar.disableProperty().bind(Bindings.createBooleanBinding(() -> vs.isInvalid(), vs.invalidProperty()));
    }

    private void fillForm(Orden orden) {
        txtIdOrden.setText(orden.getIdOrden() != null ? orden.getIdOrden().toString() : "");
        txtIdPresentacion.setText(orden.getIdPresentacion() != null ? orden.getIdPresentacion().toString() : "");
        txtCantidad.setText(orden.getCantidad() != null ? orden.getCantidad().toString() : "");
        dpFechaOrden.setValue(orden.getFecha_orden() != null ? orden.getFecha_orden().toLocalDate() : null);
        dpFechaDespacho.setValue(orden.getFecha_despacho() != null ? orden.getFecha_despacho().toLocalDate() : null);
    }

    @FXML
    public void onNuevo() { onLimpiar(); }

    @FXML
    public void onGuardar() {
        try {
            Integer idPresentacion = Integer.parseInt(txtIdPresentacion.getText().trim());
            Integer cantidad = Integer.parseInt(txtCantidad.getText().trim());
            LocalDate fechaOrdenLocal = dpFechaOrden.getValue();
            LocalDate fechaDespachoLocal = dpFechaDespacho.getValue();
            Date fechaOrden = fechaOrdenLocal != null ? Date.valueOf(fechaOrdenLocal) : null;
            Date fechaDespacho = fechaDespachoLocal != null ? Date.valueOf(fechaDespachoLocal) : null;

            Orden orden;
            if (!txtIdOrden.getText().isBlank()) {
                // Actualizar existente
                Integer idOrden = Integer.parseInt(txtIdOrden.getText().trim());
                orden = new Orden(idOrden, idPresentacion, cantidad, fechaOrden, fechaDespacho);
                dao.update(orden);
                showInfo("Actualizado", "Orden actualizada correctamente.");
            } else {
                // Insertar nuevo
                orden = new Orden(idPresentacion, cantidad, fechaOrden, fechaDespacho);
                dao.insert(orden);
                showInfo("Guardado", "Orden registrada correctamente.");
            }
            refreshTable();
            onLimpiar();
        } catch (NumberFormatException ex) {
            showError("Revisa los campos numéricos.");
        } catch (IllegalArgumentException ex) {
            showError("Revisa los campos de fecha.");
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
        dpFechaOrden.setValue(null);
        dpFechaDespacho.setValue(null);
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
