package org.cerveza.cerveza.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.Severity;
import org.cerveza.cerveza.dao.MarcaDao;
import org.cerveza.cerveza.dao.impl.MarcaDaoImpl;
import org.cerveza.cerveza.model.Marca;

public class MarcaFormController {
    @FXML private TextField txtIdFabricante, txtNombre;
    @FXML private TextArea txtDescripcion;
    @FXML private Button btnGuardar, btnNuevo, btnEliminar;
    @FXML private TableView<Marca> tblMarcas;
    @FXML private TableColumn<Marca, Integer> colId, colIdFabricante;
    @FXML private TableColumn<Marca, String> colNombre, colDescripcion;

    private final MarcaDao dao = new MarcaDaoImpl();
    private final ValidationSupport vs = new ValidationSupport();
    private Integer editingId = null;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colIdFabricante.setCellValueFactory(new PropertyValueFactory<>("idFabricante"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        refreshTable();

        vs.registerValidator(txtIdFabricante, true,
                Validator.createRegexValidator("Numérico", "^\\d+$", Severity.ERROR));
        vs.registerValidator(txtNombre, true, Validator.createEmptyValidator("Nombre requerido"));
        vs.registerValidator(txtDescripcion, true, Validator.createEmptyValidator("Descripción requerida"));

        btnGuardar.disableProperty().bind(Bindings.createBooleanBinding(vs::isInvalid, vs.invalidProperty()));

        tblMarcas.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                editingId = sel.getId();
                txtIdFabricante.setText(String.valueOf(sel.getIdFabricante()));
                txtNombre.setText(sel.getNombre());
                txtDescripcion.setText(sel.getDescripcion());
            }
        });
    }

    @FXML public void onNuevo() { clearForm(); }

    @FXML
    public void onGuardar() {
        try {
            int idFab = Integer.parseInt(txtIdFabricante.getText().trim());
            String nombre = txtNombre.getText().trim();
            String desc = txtDescripcion.getText().trim();

            Marca m = new Marca(editingId, idFab, nombre, desc);
            if (editingId == null) dao.insert(m); else dao.update(m);

            info("Guardado", "Marca guardada correctamente");
            refreshTable();
            clearForm();
        } catch (Exception ex) {
            error("Error al guardar: " + ex.getMessage());
        }
    }

    @FXML
    public void onEliminar() {
        Marca sel = tblMarcas.getSelectionModel().getSelectedItem();
        if (sel == null) { error("Selecciona una marca"); return; }
        try {
            dao.delete(sel.getId());
            refreshTable();
            clearForm();
            info("Eliminado", "Marca eliminada correctamente");
        } catch (Exception e) { error("No se pudo eliminar: " + e.getMessage()); }
    }

    @FXML
    public void onListar() {
        refreshTable();
        clearForm();
    }

    private void refreshTable() { tblMarcas.setItems(FXCollections.observableArrayList(dao.findAll())); }
    private void clearForm() {
        editingId = null;
        txtIdFabricante.clear(); txtNombre.clear(); txtDescripcion.clear();
        tblMarcas.getSelectionModel().clearSelection();
    }
    private void info(String h, String m) { Alert a = new Alert(Alert.AlertType.INFORMATION); a.setHeaderText(h); a.setContentText(m); a.showAndWait(); }
    private void error(String m) { Alert a = new Alert(Alert.AlertType.ERROR); a.setHeaderText("Validación"); a.setContentText(m); a.showAndWait(); }
}
