package org.cerveza.cerveza.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.cerveza.cerveza.dao.ProduccionDao;
import org.cerveza.cerveza.dao.impl.ProduccionDaoImpl;
import org.cerveza.cerveza.model.Produccion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

import static org.cerveza.cerveza.config.Database.getConnection;

public class ProduccionFormController {

    @FXML private ComboBox<CervezaItem> cbCerveza;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtCantidad;

    @FXML private TableView<Produccion> tblProduccion;
    @FXML private TableColumn<Produccion, Number> colId;
    @FXML private TableColumn<Produccion, String> colCerveza;
    @FXML private TableColumn<Produccion, LocalDate> colFecha;
    @FXML private TableColumn<Produccion, Number> colCantidad;

    @FXML private Button btnNuevo;
    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;

    private final ProduccionDao dao = new ProduccionDaoImpl();
    private final ObservableList<Produccion> data = FXCollections.observableArrayList();
    private final ObservableList<CervezaItem> cervezas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Tabla
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()));
        colCerveza.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCervezaNombre()));
        colFecha.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getFecha()));
        colCantidad.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getCantidad()));
        tblProduccion.setItems(data);

        // Combo Cerveza (carga directa desde tabla "cerveza")
        cbCerveza.setItems(cervezas);
        cbCerveza.setConverter(new StringConverter<>() {
            @Override public String toString(CervezaItem item) { return item == null ? "" : item.nombre; }
            @Override public CervezaItem fromString(String s) { return null; }
        });

        cargarCervezas();
        refrescarTabla();

        // Al seleccionar fila, poblar formulario
        tblProduccion.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                seleccionarEnCombo(sel.getCervezaId());
                dpFecha.setValue(sel.getFecha());
                txtCantidad.setText(String.valueOf(sel.getCantidad()));
            }
        });

        // Estado inicial
        limpiarForm();
    }

    @FXML
    public void onNuevo() {
        tblProduccion.getSelectionModel().clearSelection();
        limpiarForm();
    }

    @FXML
    public void onGuardar() {
        try {
            CervezaItem item = cbCerveza.getValue();
            if (item == null) throw new IllegalArgumentException("Selecciona una cerveza.");
            LocalDate fecha = dpFecha.getValue();
            if (fecha == null) throw new IllegalArgumentException("Selecciona una fecha.");
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser > 0.");

            Produccion sel = tblProduccion.getSelectionModel().getSelectedItem();
            if (sel == null) { // INSERT
                Produccion p = new Produccion(null, item.id, fecha, cantidad, item.nombre);
                dao.insert(p);
                data.addFirst(p);
                tblProduccion.getSelectionModel().select(p);
            } else { // UPDATE
                sel.setCervezaId(item.id);
                sel.setCervezaNombre(item.nombre);
                sel.setFecha(fecha);
                sel.setCantidad(cantidad);
                dao.update(sel);
                tblProduccion.refresh();
            }

            limpiarForm();
        } catch (NumberFormatException nfe) {
            error("Cantidad inválida", "Escribe un número entero para cantidad.");
        } catch (Exception ex) {
            error("No se pudo guardar", ex.getMessage());
        }
    }

    @FXML
    public void onEliminar() {
        Produccion sel = tblProduccion.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        if (!confirm("¿Eliminar el registro seleccionado?")) return;

        try {
            if (dao.delete(sel.getId())) {
                data.remove(sel);
                limpiarForm();
            }
        } catch (Exception ex) {
            error("No se pudo eliminar", ex.getMessage());
        }
    }

    @FXML
    public void onListar() {
        refrescarTabla();
        limpiarForm();
    }

    /* -------------------- helpers -------------------- */

    private void limpiarForm() {
        cbCerveza.getSelectionModel().clearSelection();
        dpFecha.setValue(LocalDate.now());
        txtCantidad.clear();
        btnEliminar.setDisable(tblProduccion.getSelectionModel().getSelectedItem() == null);
    }

    private void refrescarTabla() {
        try {
            data.setAll(dao.findAll());
        } catch (Exception ex) {
            error("No se pudo cargar Producción", ex.getMessage());
        }
    }

    private void cargarCervezas() {
        cervezas.clear();
        String sql = "SELECT idcerveza, nombre FROM cerveza ORDER BY nombre";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cervezas.add(new CervezaItem(rs.getInt("idcerveza"), rs.getString("nombre")));
            }
        } catch (Exception ex) {
            error("No se pudieron cargar cervezas", ex.getMessage());
        }
    }

    private void seleccionarEnCombo(Integer cervezaId) {
        if (cervezaId == null) { cbCerveza.getSelectionModel().clearSelection(); return; }
        for (CervezaItem it : cervezas) {
            if (it.id == cervezaId) { cbCerveza.getSelectionModel().select(it); return; }
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

    /* Item simple para el ComboBox */
    public static class CervezaItem {
        final int id; final String nombre;
        public CervezaItem(int id, String nombre) { this.id = id; this.nombre = nombre; }
        @Override public String toString() { return nombre; }
    }
}
