package org.cerveza.cerveza.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.Severity;
import org.cerveza.cerveza.dao.EnvaseDao;
import org.cerveza.cerveza.dao.impl.EnvaseDaoImpl;
import org.cerveza.cerveza.model.Envase;

public class EnvaseFormController {
    @FXML private TextField txtNombre, txtMaterial, txtCapacidad;
    @FXML private TextArea txtDescripcion;
    @FXML private Button btnGuardar, btnNuevo, btnEliminar;
    @FXML private TableView<Envase> tblEnvases;
    @FXML private TableColumn<Envase, Integer> colId;
    @FXML private TableColumn<Envase, String> colNombre, colMaterial, colCapacidad, colDescripcion;

    private final EnvaseDao dao = new EnvaseDaoImpl();
    private final ValidationSupport vs = new ValidationSupport();
    private Integer editingId = null;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colMaterial.setCellValueFactory(new PropertyValueFactory<>("material"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        refreshTable();

        vs.registerValidator(txtNombre, true, Validator.createEmptyValidator("Nombre requerido"));
        vs.registerValidator(txtMaterial, true, Validator.createEmptyValidator("Material requerido"));
        // Ejemplos válidos: 355ml, 6x355ml, 1L
        vs.registerValidator(txtCapacidad, true,
                Validator.createRegexValidator("Ej: 355ml, 6x355ml, 1L",
                        "^(?:\\d+(?:x\\d+)?)\\s*(?:ml|ML|l|L)$",
                        Severity.ERROR));
        vs.registerValidator(txtDescripcion, true, Validator.createEmptyValidator("Descripción requerida"));

        btnGuardar.disableProperty().bind(Bindings.createBooleanBinding(vs::isInvalid, vs.invalidProperty()));

        tblEnvases.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                editingId = sel.getId();
                txtNombre.setText(sel.getNombre());
                txtMaterial.setText(sel.getMaterial());
                txtCapacidad.setText(sel.getCapacidad());
                txtDescripcion.setText(sel.getDescripcion());
            }
        });
    }

    @FXML public void onNuevo() { clearForm(); }

    @FXML
    public void onGuardar() {
        try {
            String nombre = txtNombre.getText().trim();
            String material = txtMaterial.getText().trim();
            String capacidad = txtCapacidad.getText().trim();
            String descripcion = txtDescripcion.getText().trim();

            Envase e = new Envase(editingId, nombre, material, capacidad, descripcion);
            if (editingId == null) dao.insert(e); else dao.update(e);

            info("Guardado", "Envase guardado correctamente");
            refreshTable();
            clearForm();
        } catch (Exception ex) { error("Error al guardar: " + ex.getMessage()); }
    }

    @FXML
    public void onEliminar() {
        Envase sel = tblEnvases.getSelectionModel().getSelectedItem();
        if (sel == null) { error("Selecciona un envase"); return; }
        try {
            dao.delete(sel.getId());
            refreshTable();
            clearForm();
            info("Eliminado", "Envase eliminado correctamente");
        } catch (Exception e) { error("No se pudo eliminar: " + e.getMessage()); }
    }

    private void refreshTable() { tblEnvases.setItems(FXCollections.observableArrayList(dao.findAll())); }
    private void clearForm() {
        editingId = null;
        txtNombre.clear(); txtMaterial.clear(); txtCapacidad.clear(); txtDescripcion.clear();
        tblEnvases.getSelectionModel().clearSelection();
    }
    private void info(String h, String m) { Alert a = new Alert(Alert.AlertType.INFORMATION); a.setHeaderText(h); a.setContentText(m); a.showAndWait(); }
    private void error(String m) { Alert a = new Alert(Alert.AlertType.ERROR); a.setHeaderText("Validación"); a.setContentText(m); a.showAndWait(); }
}
