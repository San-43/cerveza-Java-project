package org.cerveza.cerveza.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.cerveza.cerveza.dao.impl.IngredienteDaoImpl;
import org.cerveza.cerveza.model.Ingrediente;
import org.controlsfx.validation.ValidationSupport;

public class IngredienteFormController {
    private Integer editingId = null;
    private final IngredienteDaoImpl dao = new IngredienteDaoImpl();
    private final ValidationSupport vs = new ValidationSupport();
    private final ObservableList<Ingrediente> data = FXCollections.observableArrayList();

    @FXML
    private TextField txtIdIngrediente;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextArea txtDescripcion;

    @FXML
    private TableView<Ingrediente> tblIngredientes;

    @FXML
    private TableColumn<Ingrediente, Number> colId;

    @FXML
    private TableColumn<Ingrediente, String> colNombre;

    @FXML
    private TableColumn<Ingrediente, String> colDescripcion;

    @FXML
    private Button btnNuevo;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnEliminar;

    // Método vinculado al onAction del botón Eliminar

    @FXML
    private void initialize() {
        System.out.println("Controlador cargado correctamente");

        // Configurar columnas
        colId.setCellValueFactory(new PropertyValueFactory<>("idIngrediente"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        // Enlazar la lista de datos con la tabla
        tblIngredientes.setItems(data);

        // Cargar los datos desde la base
        refrescarTabla();

        // Cuando seleccione un ingrediente en la tabla, cargar en los TextFields
        tblIngredientes.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                txtIdIngrediente.setText(String.valueOf(newSel.getIdIngrediente()));
                txtNombre.setText(newSel.getNombre());
                txtDescripcion.setText(newSel.getDescripcion());
                editingId = newSel.getIdIngrediente();
            }
        });
    }

    @FXML
    public void nnew(ActionEvent actionEvent) {
        tblIngredientes.getSelectionModel().clearSelection();
        clearForm();
    }

    private void clearForm() {
        editingId = null;
        txtIdIngrediente.clear(); txtNombre.clear(); txtDescripcion.clear();
        tblIngredientes.getSelectionModel().clearSelection();
    }

    public void delete(ActionEvent actionEvent) {
        Ingrediente sel = tblIngredientes.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        if (!confirm("¿Eliminar el expendio seleccionado?")) return;

        try {
            if (dao.delete(sel.getIdIngrediente())) {
                data.remove(sel);
                clearForm();
            }
        } catch (Exception ex) {
            error("No se pudo eliminar", ex.getMessage());
        }
    }

    public void save(ActionEvent actionEvent) {
        try {
            String nombre = safe(txtNombre.getText());
            if (nombre.isEmpty()) throw new IllegalArgumentException("El nombre es obligatorio.");
            String descripcion = safe(txtDescripcion.getText());
            if (descripcion.isEmpty()) throw new IllegalArgumentException("la descripcion es obligatorio.");

            Ingrediente sel = tblIngredientes.getSelectionModel().getSelectedItem();
            if (sel == null) {
                // INSERT
                Ingrediente nuevo = new Ingrediente();
                nuevo.setNombre(nombre);
                nuevo.setDescripcion(descripcion);
                dao.insert(nuevo);
                data.addFirst(nuevo);
                tblIngredientes.getSelectionModel().select(nuevo);
            } else {
                // UPDATE
                sel.setNombre(nombre);
                sel.setDescripcion(descripcion);
                dao.update(sel);
                tblIngredientes.refresh();
            }
            clearForm();
        } catch (Exception ex) {
            error("No se pudo guardar", ex.getMessage());
        }
    }

    private void error(String header, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(header);
        a.setContentText(msg);
        a.showAndWait();
    }

    private boolean confirm(String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL);
        a.setHeaderText("Confirmar");
        return a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
    private void refrescarTabla() {
        try {
            data.setAll(dao.findAll());
        } catch (Exception ex) {
            error("No se pudieron cargar los expendios", ex.getMessage());
        }
    }
    private static String safe(String s) { return s == null ? "" : s.trim(); }
    private static String nullToEmpty(String s) { return s == null ? "" : s; }
}
