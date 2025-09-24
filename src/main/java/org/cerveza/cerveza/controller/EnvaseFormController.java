package org.cerveza.cerveza.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    @FXML private Button btnGuardar, btnNuevo, btnEliminar, btnActualizar;
    @FXML private TableView<Envase> tblEnvases;
    @FXML private TableColumn<Envase, Integer> colId;
    @FXML private TableColumn<Envase, String> colNombre, colMaterial, colCapacidad, colDescripcion;
    @FXML private ComboBox<String> cmbBusqueda;
    @FXML private TextField txtBusqueda;

    private final EnvaseDao dao = new EnvaseDaoImpl();
    private final ValidationSupport vs = new ValidationSupport();
    private Integer editingId = null;
    private java.util.List<Envase> todosLosEnvases = new java.util.ArrayList<>();
    private final ObservableList<Envase> envasesEnTabla = FXCollections.observableArrayList();
    private boolean listaCompletaCargada = false;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colMaterial.setCellValueFactory(new PropertyValueFactory<>("material"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        // Opciones de búsqueda
        cmbBusqueda.setItems(FXCollections.observableArrayList("ID", "Nombre", "Material", "Capacidad", "Descripción"));
        cmbBusqueda.setPromptText("Elige un campo...");
        // Listeners de búsqueda
        cmbBusqueda.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltroBusqueda());
        txtBusqueda.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltroBusqueda());
        // Estado inicial de botones
        btnActualizar.setDisable(true);
        btnGuardar.setDisable(false);
        btnEliminar.setDisable(true);
        // Validaciones
        vs.registerValidator(txtNombre, true, Validator.createEmptyValidator("Nombre requerido"));
        vs.registerValidator(txtMaterial, true, Validator.createEmptyValidator("Material requerido"));
        vs.registerValidator(txtCapacidad, true,
                Validator.createRegexValidator("Ej: 355ml, 6x355ml, 1L",
                        "^(?:\\d+(?:x\\d+)?)\\s*(?:ml|ML|l|L)$",
                        Severity.ERROR));
        vs.registerValidator(txtDescripcion, true, Validator.createEmptyValidator("Descripción requerida"));
        // Selección en tabla
        tblEnvases.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                editingId = sel.getId();
                txtNombre.setText(sel.getNombre());
                txtMaterial.setText(sel.getMaterial());
                txtCapacidad.setText(sel.getCapacidad());
                txtDescripcion.setText(sel.getDescripcion());
                btnActualizar.setDisable(false);
                btnGuardar.setDisable(true);
                btnEliminar.setDisable(false);
            } else {
                editingId = null;
                btnActualizar.setDisable(true);
                btnGuardar.setDisable(false);
                btnEliminar.setDisable(true);
            }
        });
        // No cargar registros al iniciar
        tblEnvases.setItems(envasesEnTabla);
        actualizarPlaceholder();
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
    public void onActualizar() {
        Envase sel = tblEnvases.getSelectionModel().getSelectedItem();
        if (sel == null) { error("Selecciona un envase para actualizar"); return; }
        try {
            String nombre = txtNombre.getText().trim();
            String material = txtMaterial.getText().trim();
            String capacidad = txtCapacidad.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            boolean hayCambios =
                !nombre.equals(sel.getNombre()) ||
                !material.equals(sel.getMaterial()) ||
                !capacidad.equals(sel.getCapacidad()) ||
                !descripcion.equals(sel.getDescripcion());
            if (!hayCambios) {
                info("Sin cambios", "No hay cambios por guardar.");
                return;
            }
            Envase e = new Envase(sel.getId(), nombre, material, capacidad, descripcion);
            dao.update(e);
            info("Actualizado", "Envase actualizado correctamente");
            refreshTable();
            clearForm();
        } catch (Exception ex) { error("Error al actualizar: " + ex.getMessage()); }
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

    private void refreshTable() {
        todosLosEnvases = dao.findAll();
        if (listaCompletaCargada) {
            envasesEnTabla.setAll(todosLosEnvases);
            actualizarPlaceholder();
        }
    }
    private void aplicarFiltroBusqueda() {
        String campo = cmbBusqueda.getValue();
        String texto = txtBusqueda.getText();
        if (campo == null || texto == null || texto.isBlank()) {
            if (listaCompletaCargada) {
                envasesEnTabla.setAll(todosLosEnvases);
                actualizarPlaceholder();
            } else {
                envasesEnTabla.clear();
                actualizarPlaceholder();
            }
            return;
        }
        if (todosLosEnvases == null || todosLosEnvases.isEmpty()) {
            todosLosEnvases = dao.findAll();
        }
        java.util.List<Envase> resultados = new java.util.ArrayList<>();
        for (Envase e : todosLosEnvases) {
            switch (campo) {
                case "ID":
                    if (String.valueOf(e.getId()).contains(texto)) resultados.add(e);
                    break;
                case "Nombre":
                    if (e.getNombre() != null && e.getNombre().toLowerCase().contains(texto.toLowerCase())) resultados.add(e);
                    break;
                case "Material":
                    if (e.getMaterial() != null && e.getMaterial().toLowerCase().contains(texto.toLowerCase())) resultados.add(e);
                    break;
                case "Capacidad":
                    if (e.getCapacidad() != null && e.getCapacidad().toLowerCase().contains(texto.toLowerCase())) resultados.add(e);
                    break;
                case "Descripción":
                    if (e.getDescripcion() != null && e.getDescripcion().toLowerCase().contains(texto.toLowerCase())) resultados.add(e);
                    break;
            }
        }
        envasesEnTabla.setAll(resultados);
        if (envasesEnTabla.isEmpty()) {
            tblEnvases.setPlaceholder(new Label("No se encontró en la base de datos"));
        } else {
            tblEnvases.setPlaceholder(new Label(" "));
        }
    }
    private void clearForm() {
        editingId = null;
        txtNombre.clear(); txtMaterial.clear(); txtCapacidad.clear(); txtDescripcion.clear();
        tblEnvases.getSelectionModel().clearSelection();
        txtBusqueda.clear();
        cmbBusqueda.getSelectionModel().clearSelection();
        btnActualizar.setDisable(true);
        btnGuardar.setDisable(false);
        btnEliminar.setDisable(true);
    }
    @FXML
    private void onListar() {
        todosLosEnvases = dao.findAll();
        listaCompletaCargada = true;
        envasesEnTabla.setAll(todosLosEnvases);
        actualizarPlaceholder();
        tblEnvases.getSelectionModel().clearSelection();
        cmbBusqueda.getSelectionModel().clearSelection();
        txtBusqueda.clear();
    }
    private void info(String h, String m) { Alert a = new Alert(Alert.AlertType.INFORMATION); a.setHeaderText(h); a.setContentText(m); a.showAndWait(); }
    private void error(String m) { Alert a = new Alert(Alert.AlertType.ERROR); a.setHeaderText("Validación"); a.setContentText(m); a.showAndWait(); }
    private void actualizarPlaceholder() {
        if (!envasesEnTabla.isEmpty()) {
            tblEnvases.setPlaceholder(new Label(" "));
        } else if (listaCompletaCargada) {
            tblEnvases.setPlaceholder(new Label("No hay registros disponibles"));
        } else {
            tblEnvases.setPlaceholder(new Label("Realiza una búsqueda para ver resultados"));
        }
    }
}
