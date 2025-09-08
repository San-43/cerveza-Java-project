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

    private final ExpendioDao dao = new ExpendioDaoImpl();
    private final ObservableList<Expendio> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdexpendio()));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(nullToEmpty(c.getValue().getNombre())));
        colUbicacion.setCellValueFactory(c -> new SimpleStringProperty(nullToEmpty(c.getValue().getUbicacion())));
        colRFC.setCellValueFactory(c -> new SimpleStringProperty(nullToEmpty(c.getValue().getRfc())));
        colResponsable.setCellValueFactory(c -> new SimpleStringProperty(nullToEmpty(c.getValue().getResponsable())));
        tblExpendios.setItems(data);

        tblExpendios.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            btnEliminar.setDisable(sel == null);
            if (sel == null) return;
            txtNombre.setText(sel.getNombre());
            txtUbicacion.setText(sel.getUbicacion());
            txtRFC.setText(sel.getRfc());
            txtResponsable.setText(sel.getResponsable());
        });

        refrescarTabla();
        limpiarForm();
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
            String rfc = safe(txtRFC.getText()).toUpperCase();
            String responsable = safe(txtResponsable.getText());

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

    /* -------- helpers -------- */

    private void refrescarTabla() {
        try {
            data.setAll(dao.findAll());
        } catch (Exception ex) {
            error("No se pudieron cargar los expendios", ex.getMessage());
        }
    }

    private void limpiarForm() {
        txtNombre.clear();
        txtUbicacion.clear();
        txtRFC.clear();
        txtResponsable.clear();
        btnEliminar.setDisable(true);
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
}
