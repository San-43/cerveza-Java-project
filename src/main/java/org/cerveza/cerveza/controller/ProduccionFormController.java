package org.cerveza.cerveza.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.cerveza.cerveza.dao.CervezaDao;
import org.cerveza.cerveza.dao.ProduccionDao;
import org.cerveza.cerveza.dao.impl.CervezaDaoImpl;
import org.cerveza.cerveza.dao.impl.ProduccionDaoImpl;
import org.cerveza.cerveza.model.Cerveza;
import org.cerveza.cerveza.model.Produccion;

import java.time.LocalDate;

public class ProduccionFormController {

    // Tabla
    @FXML private TableView<Produccion> tblProduccion;
    @FXML private TableColumn<Produccion, Integer> colId;
    @FXML private TableColumn<Produccion, String> colCerveza;
    @FXML private TableColumn<Produccion, LocalDate> colFecha;
    @FXML private TableColumn<Produccion, Integer> colCantidad;
    @FXML private TableColumn<Produccion, String> colLote;

    // Formulario
    @FXML private ComboBox<Cerveza> cbCerveza;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtLote;
    @FXML private Button btnGuardar;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnRefrescar;

    private final ProduccionDao produccionDAO = new ProduccionDaoImpl();
    private final CervezaDao cervezaDAO = new CervezaDaoImpl(); // Se asume existente
    private final ObservableList<Produccion> data = FXCollections.observableArrayList();
    private Produccion editing; // null = inserción

    @FXML
    public void initialize() {
        // columnas
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCerveza.setCellValueFactory(new PropertyValueFactory<>("cervezaNombre"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colLote.setCellValueFactory(new PropertyValueFactory<>("lote"));

        // combos
        cbCerveza.setItems(FXCollections.observableArrayList(cervezaDAO.findAll()));
        cbCerveza.setConverter(new StringConverter<>() {
            @Override public String toString(Cerveza c) { return c == null ? "" : c.getNombre(); }
            @Override public Cerveza fromString(String s) { return null; }
        });

        // tabla
        tblProduccion.setItems(data);
        tblProduccion.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> loadToForm(sel));

        dpFecha.setValue(LocalDate.now());
        refresh();
    }

    private void loadToForm(Produccion p) {
        if (p == null) return;
        this.editing = p;
        // seleccionar cerveza por id
        cbCerveza.getItems().stream()
                .filter(c -> c.getId().equals(p.getCervezaId()))
                .findFirst().ifPresent(cbCerveza::setValue);
        dpFecha.setValue(p.getFecha());
        txtCantidad.setText(String.valueOf(p.getCantidad()));
        txtLote.setText(p.getLote() == null ? "" : p.getLote());
    }

    @FXML
    public void onNuevo() {
        editing = null;
        cbCerveza.setValue(null);
        dpFecha.setValue(LocalDate.now());
        txtCantidad.clear();
        txtLote.clear();
        tblProduccion.getSelectionModel().clearSelection();
    }

    @FXML
    public void onGuardar() {
        try {
            Cerveza c = cbCerveza.getValue();
            if (c == null) throw new IllegalArgumentException("Selecciona una cerveza");
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            LocalDate fecha = dpFecha.getValue();
            String lote = txtLote.getText().isBlank() ? null : txtLote.getText().trim();

            if (editing == null) {
                Produccion p = new Produccion(null, c.getId(), c.getNombre(), fecha, cantidad, lote);
                produccionDAO.insert(p);
            } else {
                editing.setCervezaId(c.getId());
                editing.setCervezaNombre(c.getNombre());
                editing.setFecha(fecha);
                editing.setCantidad(cantidad);
                editing.setLote(lote);
                produccionDAO.update(editing);
            }
            refresh();
            onNuevo();
        } catch (NumberFormatException nfe) {
            showError("Cantidad inválida (usa números enteros).");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    @FXML
    public void onEliminar() {
        Produccion sel = tblProduccion.getSelectionModel().getSelectedItem();
        if (sel == null) { showError("Selecciona un registro."); return; }
        try {
            produccionDAO.delete(sel.getId());
            refresh();
            onNuevo();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    @FXML
    public void onRefrescar() { refresh(); }

    private void refresh() {
        data.setAll(produccionDAO.findAll());
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText("Error");
        a.showAndWait();
    }
}
