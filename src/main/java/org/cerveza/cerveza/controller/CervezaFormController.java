package org.cerveza.cerveza.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.cerveza.cerveza.dao.CervezaDao;
import org.cerveza.cerveza.dao.impl.CervezaDaoImpl;
import org.cerveza.cerveza.model.Cerveza;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

public class CervezaFormController {
    @FXML
    private TextField txtIdMarca, txtNombre, txtAspecto, txtGraduacion;
    @FXML private TextArea txtProcedimientos;
    @FXML private Button btnGuardar;
    @FXML private TableView<Cerveza> tblCervezas;
    @FXML private TableColumn<Cerveza, Integer> colId, colIdMarca;
    @FXML private TableColumn<Cerveza, String> colNombre, colAspecto;
    @FXML private TableColumn<Cerveza, Double> colGraduacion;

    private final CervezaDao dao = new CervezaDaoImpl();
    private final ValidationSupport vs = new ValidationSupport();

    @FXML
    public void initialize() {
// Tabla
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colIdMarca.setCellValueFactory(new PropertyValueFactory<>("idMarca"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colAspecto.setCellValueFactory(new PropertyValueFactory<>("aspecto"));
        colGraduacion.setCellValueFactory(new PropertyValueFactory<>("graduacion"));
        refreshTable();


// Validaciones (obligatorios + formatos)
        vs.registerValidator(txtIdMarca, true, Validator.createRegexValidator("ID marca numérico", "^\\d+$", Severity.ERROR));
        vs.registerValidator(txtNombre, true, Validator.createEmptyValidator("Nombre requerido"));
        vs.registerValidator(txtAspecto, true, Validator.createEmptyValidator("Aspecto requerido"));
        vs.registerValidator(txtProcedimientos, true, Validator.createEmptyValidator("Procedimientos requerido"));
        vs.registerValidator(txtGraduacion, false, Validator.createRegexValidator("Formato decimal ej. 4.5", "^(?:\\d{1,2})(?:\\.\\d{1,2})?$", Severity.ERROR));


// Deshabilita Guardar si hay errores
        btnGuardar.disableProperty().bind(Bindings.createBooleanBinding(
                () -> vs.isInvalid(), vs.invalidProperty()));


// Formateador decimal (opcional, permite vacío)
        txtGraduacion.setTextFormatter(new TextFormatter<>(new StringConverter<String>() {
            @Override public String toString(String object) { return object; }
            @Override public String fromString(String string) { return string; }
        }));
    }

    @FXML public void onNuevo() { onLimpiar(); }


    @FXML
    public void onGuardar() {
        try {
            Integer idMarca = Integer.parseInt(txtIdMarca.getText().trim());
            String nombre = txtNombre.getText().trim();
            String aspecto = txtAspecto.getText().trim();
            String proc = txtProcedimientos.getText().trim();
            Double grad = txtGraduacion.getText().isBlank() ? null : Double.parseDouble(txtGraduacion.getText());


// Reglas adicionales
            if (grad != null && (grad < 0 || grad > 25)) {
                showError("La graduación debe estar entre 0 y 25%");
                return;
            }


            Cerveza c = new Cerveza(null, idMarca, nombre, aspecto, proc, grad);
            dao.insert(c);
            showInfo("Guardado", "Cerveza registrada correctamente.");
            refreshTable();
            onLimpiar();
        } catch (NumberFormatException ex) {
            showError("Revisa los campos numéricos.");
        } catch (Exception ex) {
            showError("Error al guardar: " + ex.getMessage());
        }
    }

    @FXML public void onLimpiar() {
        txtIdMarca.clear();
        txtNombre.clear();
        txtAspecto.clear();
        txtProcedimientos.clear();
        txtGraduacion.clear();
    }


    private void refreshTable() {
        tblCervezas.setItems(FXCollections.observableArrayList(dao.findAll()));
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
