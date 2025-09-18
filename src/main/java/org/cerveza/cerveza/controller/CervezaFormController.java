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

import java.util.Objects;

public class CervezaFormController {

    @FXML
    private ComboBox<Integer> cmbIdMarca;
    @FXML private TextField txtNombre, txtAspecto, txtGraduacion;
    @FXML private TextArea txtProcedimientos;
    @FXML private Button btnGuardar, btnActualizar, btnEliminar;
    @FXML private TableView<Cerveza> tblCervezas;
    @FXML private TableColumn<Cerveza, Integer> colId, colIdMarca;
    @FXML private TableColumn<Cerveza, String> colNombre, colAspecto;
    @FXML private TableColumn<Cerveza, Double> colGraduacion;
    @FXML private TableColumn<Cerveza, Integer> colExistenciaTotal;
    @FXML private ComboBox<String> cmbBusqueda;
    @FXML private TextField txtBusqueda;

    private final CervezaDao dao = new CervezaDaoImpl();
    private final org.cerveza.cerveza.dao.MarcaDao marcaDao = new org.cerveza.cerveza.dao.impl.MarcaDaoImpl();
    private final ValidationSupport vs = new ValidationSupport();
    private Cerveza cervezaSeleccionada = null;

    @FXML
    public void initialize() {
        // Tabla
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colIdMarca.setCellValueFactory(new PropertyValueFactory<>("idMarca"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colAspecto.setCellValueFactory(new PropertyValueFactory<>("aspecto"));
        colGraduacion.setCellValueFactory(new PropertyValueFactory<>("graduacion"));
        colExistenciaTotal.setCellValueFactory(new PropertyValueFactory<>("existenciaTotal"));
        // No cargar registros al iniciar
        tblCervezas.setItems(FXCollections.observableArrayList());
        tblCervezas.setPlaceholder(new Label("Realiza una búsqueda para ver resultados"));

        // Poblar ComboBox de marcas
        cmbIdMarca.setItems(FXCollections.observableArrayList(
            marcaDao.findAll().stream().map(m -> m.getId()).toList()
        ));
        cmbIdMarca.setPromptText("Selecciona una marca");

        // Poblar ComboBox de búsqueda
        cmbBusqueda.setItems(FXCollections.observableArrayList(
            "ID", "ID de Marca", "Nombre", "Aspecto", "Graduación", "Existencia Total"
        ));
        cmbBusqueda.setPromptText("Elige un campo...");

        // Validaciones (obligatorios + formatos)
        vs.registerValidator(cmbIdMarca, true, Validator.createEmptyValidator("Marca requerida"));
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

        // --- NUEVO: Cargar datos al seleccionar una fila de la tabla ---
        tblCervezas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            cervezaSeleccionada = newSel;
            if (newSel != null) {
                cmbIdMarca.setValue(newSel.getIdMarca());
                txtNombre.setText(newSel.getNombre());
                txtAspecto.setText(newSel.getAspecto());
                txtProcedimientos.setText(newSel.getProcedimientos());
                txtGraduacion.setText(newSel.getGraduacion() != null ? newSel.getGraduacion().toString() : "");
                btnActualizar.setDisable(false);
                btnEliminar.setDisable(false);
                btnGuardar.setDisable(true);
            } else {
                btnActualizar.setDisable(true);
                btnEliminar.setDisable(true);
                btnGuardar.setDisable(false);
            }
        });

        // Listener para búsqueda
        cmbBusqueda.valueProperty().addListener((obs, oldVal, newVal) -> buscarYActualizarTabla());
        txtBusqueda.textProperty().addListener((obs, oldVal, newVal) -> buscarYActualizarTabla());
    }

    @FXML public void onNuevo() { onLimpiar(); }


    @FXML
    public void onGuardar() {
        try {
            Integer idMarca = cmbIdMarca.getValue();
            if (idMarca == null) {
                showError("Selecciona una marca válida.");
                return;
            }
            String nombre = txtNombre.getText().trim();
            String aspecto = txtAspecto.getText().trim();
            String proc = txtProcedimientos.getText().trim();
            Double grad = txtGraduacion.getText().isBlank() ? null : Double.parseDouble(txtGraduacion.getText());


            // Reglas adicionales
            if (grad != null && (grad < 0 || grad > 25)) {
                showError("La graduación debe estar entre 0 y 25%");
                return;
            }


            Cerveza c = new Cerveza(null, idMarca, nombre, aspecto, proc, grad, null);
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

    @FXML
    public void onActualizar() {
        if (cervezaSeleccionada == null) {
            showError("Selecciona un registro para actualizar.");
            return;
        }
        // Comprobar si hay cambios
        boolean hayCambios = false;
        Integer idMarca = cmbIdMarca.getValue();
        String nombre = txtNombre.getText().trim();
        String aspecto = txtAspecto.getText().trim();
        String proc = txtProcedimientos.getText().trim();
        Double grad = txtGraduacion.getText().isBlank() ? null : Double.parseDouble(txtGraduacion.getText());
        if (!Objects.equals(idMarca, cervezaSeleccionada.getIdMarca()) ||
            !Objects.equals(nombre, cervezaSeleccionada.getNombre()) ||
            !Objects.equals(aspecto, cervezaSeleccionada.getAspecto()) ||
            !Objects.equals(proc, cervezaSeleccionada.getProcedimientos()) ||
            !Objects.equals(grad, cervezaSeleccionada.getGraduacion())) {
            hayCambios = true;
        }
        if (!hayCambios) {
            showInfo("Sin cambios", "No hay cambios por guardar.");
            return;
        }
        // Validaciones
        if (idMarca == null) {
            showError("Selecciona una marca válida.");
            return;
        }
        if (nombre.isBlank() || aspecto.isBlank() || proc.isBlank()) {
            showError("Completa todos los campos obligatorios.");
            return;
        }
        if (grad != null && (grad < 0 || grad > 25)) {
            showError("La graduación debe estar entre 0 y 25%");
            return;
        }
        // Actualizar registro
        Cerveza c = new Cerveza(cervezaSeleccionada.getId(), idMarca, nombre, aspecto, proc, grad, cervezaSeleccionada.getExistenciaTotal());
        dao.update(c);
        showInfo("Actualizado", "Registro actualizado correctamente.");
        refreshTable();
        onLimpiar();
    }

    @FXML
    public void onEliminar() {
        if (cervezaSeleccionada == null) {
            showError("Selecciona un registro para eliminar.");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Estás seguro de eliminar este registro?");
        alert.setContentText("Esta acción no se puede deshacer.");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            dao.delete(cervezaSeleccionada.getId());
            showInfo("Eliminado", "Registro eliminado correctamente.");
            refreshTable();
            onLimpiar();
        }
    }

    @FXML public void onLimpiar() {
        cmbIdMarca.getSelectionModel().clearSelection();
        txtNombre.clear();
        txtAspecto.clear();
        txtProcedimientos.clear();
        txtGraduacion.clear();
        // Limpiar búsqueda
        txtBusqueda.clear();
        cmbBusqueda.getSelectionModel().clearSelection();
        // Limpiar selección y botones
        tblCervezas.getSelectionModel().clearSelection();
        cervezaSeleccionada = null;
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);
        btnGuardar.setDisable(false);
    }


    private void refreshTable() {
        // No cargar registros por defecto
        tblCervezas.setItems(FXCollections.observableArrayList());
        tblCervezas.setPlaceholder(new Label("Realiza una búsqueda para ver resultados"));
    }

    private void buscarYActualizarTabla() {
        String campo = cmbBusqueda.getValue();
        String texto = txtBusqueda.getText();
        if (campo == null || texto == null || texto.isBlank()) {
            refreshTable();
            return;
        }
        java.util.List<Cerveza> resultados = java.util.Collections.emptyList();
        try {
            switch (campo) {
                case "ID" -> resultados = dao.findByIdLike(texto);
                case "ID de Marca" -> resultados = dao.findByIdMarcaLike(texto);
                case "Nombre" -> resultados = dao.findByNombreLike(texto);
                case "Aspecto" -> resultados = dao.findByAspectoLike(texto);
                case "Graduación" -> resultados = dao.findByGraduacionLike(texto);
                case "Existencia Total" -> resultados = dao.findByExistenciaTotalLike(texto);
            }
        } catch (Exception e) {
            resultados = java.util.Collections.emptyList();
        }

        if (resultados == null || resultados.isEmpty()) {
            tblCervezas.setItems(FXCollections.observableArrayList());
            tblCervezas.setPlaceholder(new Label("No se encontró en la base de datos"));
        } else {
            tblCervezas.setItems(FXCollections.observableArrayList(resultados));
            tblCervezas.setPlaceholder(new Label(""));
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
