package org.cerveza.cerveza.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.cerveza.cerveza.dao.ExpendioDao;
import org.cerveza.cerveza.dao.impl.ExpendioDaoImpl;
import org.cerveza.cerveza.model.Expendio;

public class ExpendioFormController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtUbicacion;
    @FXML private TextField txtRFC;
    @FXML private TextField txtResponsable;

    @FXML private TableView<Expendio> tblExpendios;
    @FXML private TableColumn<Expendio, Number> colId;
    @FXML private TableColumn<Expendio, String> colNombre;
    @FXML private TableColumn<Expendio, String> colUbicacion;
    @FXML private TableColumn<Expendio, String> colRFC;
    @FXML private TableColumn<Expendio, String> colResponsable;

    @FXML private Button btnNuevo;
    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;
    @FXML private ComboBox<String> cmbBusqueda;
    @FXML private TextField txtBusqueda;
    @FXML private Button btnActualizar;

    private final ExpendioDao dao = new ExpendioDaoImpl();
    private final ObservableList<Expendio> data = FXCollections.observableArrayList();
    private java.util.List<Expendio> todosLosExpendios = new java.util.ArrayList<>();
    private Expendio expendioSeleccionado = null;
    private boolean listaCompletaCargada = false;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdexpendio()));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(nullToEmpty(c.getValue().getNombre())));
        colUbicacion.setCellValueFactory(c -> new SimpleStringProperty(nullToEmpty(c.getValue().getUbicacion())));
        colRFC.setCellValueFactory(c -> new SimpleStringProperty(nullToEmpty(c.getValue().getRfc())));
        colResponsable.setCellValueFactory(c -> new SimpleStringProperty(nullToEmpty(c.getValue().getResponsable())));
        tblExpendios.setItems(data);
        // Buscador
        cmbBusqueda.setItems(FXCollections.observableArrayList("ID", "Nombre", "Ubicación", "RFC", "Responsable"));
        cmbBusqueda.setPromptText("Elige un campo...");
        cmbBusqueda.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltroBusqueda());
        txtBusqueda.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltroBusqueda());
        // Estado inicial de botones
        btnActualizar.setDisable(true);
        btnGuardar.setDisable(false);
        btnEliminar.setDisable(true);
        // Selección en tabla
        tblExpendios.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            expendioSeleccionado = sel;
            if (sel != null) {
                txtNombre.setText(sel.getNombre());
                txtUbicacion.setText(sel.getUbicacion());
                txtRFC.setText(sel.getRfc());
                txtResponsable.setText(sel.getResponsable());
                btnActualizar.setDisable(false);
                btnGuardar.setDisable(true);
                btnEliminar.setDisable(false);
            } else {
                btnActualizar.setDisable(true);
                btnGuardar.setDisable(false);
                btnEliminar.setDisable(true);
            }
        });
        // No cargar registros al iniciar
        data.clear();
        actualizarPlaceholder();
    }

    @FXML
    public void onNuevo() {
        tblExpendios.getSelectionModel().clearSelection();
        limpiarForm();
    }

    @FXML
    public void onGuardar() {
        try {
            String nombre = safe(txtNombre.getText());
            if (nombre.isEmpty()) throw new IllegalArgumentException("El nombre es obligatorio.");
            String ubicacion = safe(txtUbicacion.getText());
            if (ubicacion.isEmpty()) throw new IllegalArgumentException("La ubicación es obligatoria.");
            String rfc = safe(txtRFC.getText()).toUpperCase();
            if (rfc.isEmpty()) throw new IllegalArgumentException("El rfc es obligatorio.");
            String responsable = safe(txtResponsable.getText());
            if (responsable.isEmpty()) throw new IllegalArgumentException("El responsable es obligatorio.");

            Expendio sel = tblExpendios.getSelectionModel().getSelectedItem();
            if (sel == null) {
                // INSERT
                Expendio nuevo = new Expendio();
                nuevo.setNombre(nombre);
                nuevo.setUbicacion(ubicacion);
                nuevo.setRfc(rfc);
                nuevo.setResponsable(responsable);
                dao.insert(nuevo);
                data.add(0, nuevo);
                tblExpendios.getSelectionModel().select(nuevo);
            } else {
                // UPDATE
                sel.setNombre(nombre);
                sel.setUbicacion(ubicacion);
                sel.setRfc(rfc);
                sel.setResponsable(responsable);
                dao.update(sel);
                tblExpendios.refresh();
            }
            limpiarForm();
        } catch (Exception ex) {
            error("No se pudo guardar", ex.getMessage());
        }
    }

    @FXML
    public void onEliminar() {
        Expendio sel = tblExpendios.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        if (!confirm("¿Eliminar el expendio seleccionado?")) return;

        try {
            if (dao.delete(sel.getIdexpendio())) {
                data.remove(sel);
                limpiarForm();
            }
        } catch (Exception ex) {
            error("No se pudo eliminar", ex.getMessage());
        }
    }

    @FXML
    public void onActualizar() {
        Expendio sel = tblExpendios.getSelectionModel().getSelectedItem();
        if (sel == null) { error("Selecciona un expendio para actualizar", ""); return; }
        try {
            String nombre = safe(txtNombre.getText());
            String ubicacion = safe(txtUbicacion.getText());
            String rfc = safe(txtRFC.getText()).toUpperCase();
            String responsable = safe(txtResponsable.getText());
            boolean hayCambios =
                !nombre.equals(sel.getNombre()) ||
                !ubicacion.equals(sel.getUbicacion()) ||
                !rfc.equals(sel.getRfc()) ||
                !responsable.equals(sel.getResponsable());
            if (!hayCambios) {
                info("Sin cambios", "No hay cambios por guardar.");
                return;
            }
            sel.setNombre(nombre);
            sel.setUbicacion(ubicacion);
            sel.setRfc(rfc);
            sel.setResponsable(responsable);
            dao.update(sel);
            tblExpendios.refresh();
            info("Actualizado", "Expendio actualizado correctamente");
            limpiarForm();
        } catch (Exception ex) {
            error("Error al actualizar", ex.getMessage());
        }
    }

    /* -------- helpers -------- */

    private void refrescarTabla() {
        try {
            todosLosExpendios = dao.findAll();
            if (listaCompletaCargada) {
                data.setAll(todosLosExpendios);
                actualizarPlaceholder();
            }
        } catch (Exception ex) {
            error("No se pudieron cargar los expendios", ex.getMessage());
            todosLosExpendios = new java.util.ArrayList<>();
        }
    }
    private void aplicarFiltroBusqueda() {
        String campo = cmbBusqueda.getValue();
        String texto = txtBusqueda.getText();
        if (campo == null || texto == null || texto.isBlank()) {
            if (listaCompletaCargada) {
                data.setAll(todosLosExpendios);
                actualizarPlaceholder();
            } else {
                data.clear();
                actualizarPlaceholder();
            }
            return;
        }
        if (todosLosExpendios == null || todosLosExpendios.isEmpty()) {
            refrescarTabla();
        }
        java.util.List<Expendio> resultados = new java.util.ArrayList<>();
        for (Expendio e : todosLosExpendios) {
            switch (campo) {
                case "ID":
                    if (String.valueOf(e.getIdexpendio()).contains(texto)) resultados.add(e);
                    break;
                case "Nombre":
                    if (e.getNombre() != null && e.getNombre().toLowerCase().contains(texto.toLowerCase())) resultados.add(e);
                    break;
                case "Ubicación":
                    if (e.getUbicacion() != null && e.getUbicacion().toLowerCase().contains(texto.toLowerCase())) resultados.add(e);
                    break;
                case "RFC":
                    if (e.getRfc() != null && e.getRfc().toLowerCase().contains(texto.toLowerCase())) resultados.add(e);
                    break;
                case "Responsable":
                    if (e.getResponsable() != null && e.getResponsable().toLowerCase().contains(texto.toLowerCase())) resultados.add(e);
                    break;
            }
        }
        data.setAll(resultados);
        if (data.isEmpty()) {
            tblExpendios.setPlaceholder(new Label("No se encontró en la base de datos"));
        } else {
            tblExpendios.setPlaceholder(new Label(" "));
        }
    }
    private void limpiarForm() {
        txtNombre.clear();
        txtUbicacion.clear();
        txtRFC.clear();
        txtResponsable.clear();
        tblExpendios.getSelectionModel().clearSelection();
        txtBusqueda.clear();
        cmbBusqueda.getSelectionModel().clearSelection();
        btnActualizar.setDisable(true);
        btnGuardar.setDisable(false);
        btnEliminar.setDisable(true);
    }
    @FXML
    private void onListar() {
        try {
            todosLosExpendios = dao.findAll();
            listaCompletaCargada = true;
            data.setAll(todosLosExpendios);
            actualizarPlaceholder();
            tblExpendios.getSelectionModel().clearSelection();
            cmbBusqueda.getSelectionModel().clearSelection();
            txtBusqueda.clear();
        } catch (Exception ex) {
            error("No se pudieron cargar los expendios", ex.getMessage());
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

    private static String safe(String s) { return s == null ? "" : s.trim(); }
    private static String nullToEmpty(String s) { return s == null ? "" : s; }
    private void info(String h, String m) { Alert a = new Alert(Alert.AlertType.INFORMATION); a.setHeaderText(h); a.setContentText(m); a.showAndWait(); }
    private void actualizarPlaceholder() {
        if (!data.isEmpty()) {
            tblExpendios.setPlaceholder(new Label(" "));
        } else if (listaCompletaCargada) {
            tblExpendios.setPlaceholder(new Label("No hay registros disponibles"));
        } else {
            tblExpendios.setPlaceholder(new Label("Realiza una búsqueda para ver resultados"));
        }
    }
}
