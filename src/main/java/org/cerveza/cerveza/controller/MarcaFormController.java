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
import org.cerveza.cerveza.dao.FabricanteDao;
import org.cerveza.cerveza.dao.impl.FabricanteDaoImpl;
import org.cerveza.cerveza.model.Fabricante;

public class MarcaFormController {
    @FXML private ComboBox<Fabricante> cmbIdFabricante;
    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;
    @FXML private Button btnGuardar, btnNuevo, btnEliminar, btnActualizar;
    @FXML private TableView<Marca> tblMarcas;
    @FXML private TableColumn<Marca, Integer> colId, colIdFabricante;
    @FXML private TableColumn<Marca, String> colNombre, colDescripcion;
    @FXML private ComboBox<String> cmbBusqueda;
    @FXML private TextField txtBusqueda;

    private final MarcaDao dao = new MarcaDaoImpl();
    private final FabricanteDao fabricanteDao = new FabricanteDaoImpl();
    private final ValidationSupport vs = new ValidationSupport();
    private Integer editingId = null;
    private java.util.List<Marca> todasLasMarcas = new java.util.ArrayList<>();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colIdFabricante.setCellValueFactory(new PropertyValueFactory<>("idFabricante"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        // Opciones de búsqueda
        cmbBusqueda.setItems(FXCollections.observableArrayList("ID", "ID Fabricante", "Nombre", "Descripción"));
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
        vs.registerValidator(txtDescripcion, true, Validator.createEmptyValidator("Descripción requerida"));
        // Poblar ComboBox de fabricantes con id y nombre
        var fabricantes = fabricanteDao.obtenerTodos();
        cmbIdFabricante.setItems(FXCollections.observableArrayList(fabricantes));
        cmbIdFabricante.setPromptText("Selecciona un fabricante");
        cmbIdFabricante.setConverter(new javafx.util.StringConverter<Fabricante>() {
            @Override
            public String toString(Fabricante f) {
                if (f == null) return "";
                return f.getIdFabricante() + " - " + f.getNombre();
            }
            @Override
            public Fabricante fromString(String s) { return null; }
        });
        // Selección en tabla
        tblMarcas.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                editingId = sel.getId();
                // Selecciona el fabricante correspondiente
                Fabricante fabSel = fabricantes.stream().filter(f -> f.getIdFabricante() == sel.getIdFabricante()).findFirst().orElse(null);
                cmbIdFabricante.setValue(fabSel);
                txtNombre.setText(sel.getNombre());
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
        tblMarcas.setItems(FXCollections.observableArrayList());
        tblMarcas.setPlaceholder(new Label("Realiza una búsqueda para ver resultados"));
    }

    @FXML public void onNuevo() { clearForm(); }

    @FXML
    public void onGuardar() {
        try {
            Fabricante fabricante = cmbIdFabricante.getValue();
            if (fabricante == null) {
                error("Selecciona un fabricante válido.");
                return;
            }
            int idFab = fabricante.getIdFabricante(); // Solo el id se guarda
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
    public void onActualizar() {
        Marca sel = tblMarcas.getSelectionModel().getSelectedItem();
        if (sel == null) { error("Selecciona una marca para actualizar"); return; }
        try {
            Fabricante fabricante = cmbIdFabricante.getValue();
            if (fabricante == null) {
                error("Selecciona un fabricante válido.");
                return;
            }
            int idFab = fabricante.getIdFabricante();
            String nombre = txtNombre.getText().trim();
            String desc = txtDescripcion.getText().trim();
            boolean hayCambios =
                idFab != sel.getIdFabricante() ||
                !nombre.equals(sel.getNombre()) ||
                !desc.equals(sel.getDescripcion());
            if (!hayCambios) {
                info("Sin cambios", "No hay cambios por guardar.");
                return;
            }
            Marca m = new Marca(sel.getId(), idFab, nombre, desc);
            dao.update(m);
            info("Actualizado", "Marca actualizada correctamente");
            refreshTable();
            clearForm();
        } catch (Exception ex) {
            error("Error al actualizar: " + ex.getMessage());
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

    private void refreshTable() {
        todasLasMarcas = dao.findAll();
    }
    private void aplicarFiltroBusqueda() {
        String campo = cmbBusqueda.getValue();
        String texto = txtBusqueda.getText();
        if (campo == null || texto == null || texto.isBlank()) {
            tblMarcas.setItems(FXCollections.observableArrayList());
            tblMarcas.setPlaceholder(new Label("Realiza una búsqueda para ver resultados"));
            return;
        }
        if (todasLasMarcas == null || todasLasMarcas.isEmpty()) {
            todasLasMarcas = dao.findAll();
        }
        java.util.List<Marca> resultados = new java.util.ArrayList<>();
        for (Marca m : todasLasMarcas) {
            switch (campo) {
                case "ID":
                    if (String.valueOf(m.getId()).contains(texto)) resultados.add(m);
                    break;
                case "ID Fabricante":
                    if (String.valueOf(m.getIdFabricante()).contains(texto)) resultados.add(m);
                    break;
                case "Nombre":
                    if (m.getNombre() != null && m.getNombre().toLowerCase().contains(texto.toLowerCase())) resultados.add(m);
                    break;
                case "Descripción":
                    if (m.getDescripcion() != null && m.getDescripcion().toLowerCase().contains(texto.toLowerCase())) resultados.add(m);
                    break;
            }
        }
        if (resultados.isEmpty()) {
            tblMarcas.setItems(FXCollections.observableArrayList());
            tblMarcas.setPlaceholder(new Label("No se encontró en la base de datos"));
        } else {
            tblMarcas.setItems(FXCollections.observableArrayList(resultados));
            tblMarcas.setPlaceholder(new Label(" "));
        }
    }
    private void clearForm() {
        editingId = null;
        cmbIdFabricante.getSelectionModel().clearSelection();
        txtNombre.clear(); txtDescripcion.clear();
        tblMarcas.getSelectionModel().clearSelection();
        txtBusqueda.clear();
        cmbBusqueda.getSelectionModel().clearSelection();
        btnActualizar.setDisable(true);
        btnGuardar.setDisable(false);
        btnEliminar.setDisable(true);
    }
    private void info(String h, String m) { Alert a = new Alert(Alert.AlertType.INFORMATION); a.setHeaderText(h); a.setContentText(m); a.showAndWait(); }
    private void error(String m) { Alert a = new Alert(Alert.AlertType.ERROR); a.setHeaderText("Validación"); a.setContentText(m); a.showAndWait(); }
}
