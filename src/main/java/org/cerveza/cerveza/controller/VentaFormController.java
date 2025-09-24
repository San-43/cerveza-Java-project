package org.cerveza.cerveza.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.cerveza.cerveza.dao.impl.VentaDaoImpl;
import org.cerveza.cerveza.model.Venta;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.time.LocalDate;

import static org.cerveza.cerveza.config.Database.getConnection;

public class VentaFormController {

    @FXML private ComboBox<ComboItem> cboExpendio;
    @FXML private ComboBox<ComboItem> cboPresentacion;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtCantidad;

    @FXML private TableView<Venta> tblVentas;
    @FXML private TableColumn<Venta, Integer> colId;
    @FXML private TableColumn<Venta, Integer> colExpendio;
    @FXML private TableColumn<Venta, Integer> colPresentacion;
    @FXML private TableColumn<Venta, LocalDate> colFecha;
    @FXML private TableColumn<Venta, Integer> colCantidad;

    @FXML private ComboBox<String> cmbBusqueda;
    @FXML private TextField txtBusqueda;
    @FXML private Button btnActualizar;
    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;

    private final VentaDaoImpl dao = new VentaDaoImpl();
    private Venta seleccionado;
    private javafx.collections.ObservableList<Venta> todasLasVentas = FXCollections.observableArrayList();
    private final javafx.collections.ObservableList<Venta> ventasEnTabla = FXCollections.observableArrayList();
    private boolean listaCompletaCargada = false;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idventa"));
        colExpendio.setCellValueFactory(new PropertyValueFactory<>("idexpendio"));
        colPresentacion.setCellValueFactory(new PropertyValueFactory<>("idpresentacion"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        cargarCombos();
        // Buscador
        cmbBusqueda.setItems(FXCollections.observableArrayList("ID", "Expendio", "Presentación", "Fecha", "Cantidad"));
        cmbBusqueda.setPromptText("Elige un campo...");
        cmbBusqueda.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltroBusqueda());
        txtBusqueda.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltroBusqueda());
        // Estado inicial de botones
        btnActualizar.setDisable(true);
        btnGuardar.setDisable(false);
        btnEliminar.setDisable(true);
        // No cargar registros al iniciar
        tblVentas.setItems(ventasEnTabla);
        actualizarPlaceholder();
        // Selección en tabla
        tblVentas.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            seleccionado = sel;
            if (sel != null) {
                selectComboById(cboExpendio, sel.getIdexpendio());
                selectComboById(cboPresentacion, sel.getIdpresentacion());
                dpFecha.setValue(sel.getFecha());
                txtCantidad.setText(String.valueOf(sel.getCantidad()));
                btnActualizar.setDisable(false);
                btnGuardar.setDisable(true);
                btnEliminar.setDisable(false);
            } else {
                btnActualizar.setDisable(true);
                btnGuardar.setDisable(false);
                btnEliminar.setDisable(true);
            }
        });
    }

    private void cargarCombos() {
        cboExpendio.setItems(FXCollections.observableArrayList(consultarIdNombre("expendio", "idexpendio")));
        // Para presentacion mostraremos "id - cerveza/envase"
        cboPresentacion.setItems(FXCollections.observableArrayList(consultarIdNombre("presentacion", "idpresentacion")));
    }

    private javafx.collections.ObservableList<ComboItem> consultarIdNombre(String tabla, String idCol) {
        var data = FXCollections.<ComboItem>observableArrayList();
        String sqlNombre = getString(tabla, idCol);
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sqlNombre);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) data.add(new ComboItem(rs.getInt("id"), rs.getString("nombre")));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    @NotNull
    private static String getString(String tabla, String idCol) {
        String etiqueta = "nombre";
        String sqlNombre = switch (tabla) {
            case "expendio" -> "SELECT idexpendio AS id, nombre FROM expendio ORDER BY nombre";
            // presentacion no tiene 'nombre'; mostramos un label armable "Presentación X"
            case "presentacion" -> "SELECT idpresentacion AS id, CONCAT('Presentación ', idpresentacion) AS nombre FROM presentacion ORDER BY idpresentacion";
            default -> "SELECT " + idCol + " AS id, " + etiqueta + " FROM " + tabla;
        };
        return sqlNombre;
    }

    private void selectComboById(ComboBox<ComboItem> combo, Integer id) {
        if (id == null) return;
        for (ComboItem item : combo.getItems()) if (item.id == id) { combo.getSelectionModel().select(item); return; }
    }

    private void refrescarTabla() {
        todasLasVentas.setAll(dao.findAll());
        if (listaCompletaCargada) {
            ventasEnTabla.setAll(todasLasVentas);
            actualizarPlaceholder();
        }
        aplicarFiltroBusqueda();
        onNuevo();
    }
    private void aplicarFiltroBusqueda() {
        String campo = cmbBusqueda.getValue();
        String texto = txtBusqueda.getText();
        if (campo == null || texto == null || texto.isBlank()) {
            if (listaCompletaCargada) {
                ventasEnTabla.setAll(todasLasVentas);
                actualizarPlaceholder();
            } else {
                ventasEnTabla.clear();
                actualizarPlaceholder();
            }
            return;
        }
        if (todasLasVentas.isEmpty()) {
            todasLasVentas.setAll(dao.findAll());
        }
        javafx.collections.ObservableList<Venta> resultados = FXCollections.observableArrayList();
        for (Venta v : todasLasVentas) {
            switch (campo) {
                case "ID":
                    if (String.valueOf(v.getIdventa()).contains(texto)) resultados.add(v);
                    break;
                case "Expendio":
                    if (String.valueOf(v.getIdexpendio()).contains(texto)) resultados.add(v);
                    break;
                case "Presentación":
                    if (String.valueOf(v.getIdpresentacion()).contains(texto)) resultados.add(v);
                    break;
                case "Fecha":
                    if (v.getFecha() != null && v.getFecha().toString().contains(texto)) resultados.add(v);
                    break;
                case "Cantidad":
                    if (String.valueOf(v.getCantidad()).contains(texto)) resultados.add(v);
                    break;
            }
        }
        ventasEnTabla.setAll(resultados);
        if (ventasEnTabla.isEmpty()) {
            tblVentas.setPlaceholder(new Label("No se encontró en la base de datos"));
        } else {
            tblVentas.setPlaceholder(new Label(" "));
        }
    }
    @FXML
    private void onNuevo() {
        tblVentas.getSelectionModel().clearSelection();
        seleccionado = null;
        cboExpendio.getSelectionModel().clearSelection();
        cboPresentacion.getSelectionModel().clearSelection();
        dpFecha.setValue(null);
        txtCantidad.clear();
        txtBusqueda.clear();
        cmbBusqueda.getSelectionModel().clearSelection();
        btnActualizar.setDisable(true);
        btnGuardar.setDisable(false);
        btnEliminar.setDisable(true);
    }
    @FXML
    private void onListar() {
        todasLasVentas.setAll(dao.findAll());
        listaCompletaCargada = true;
        ventasEnTabla.setAll(todasLasVentas);
        actualizarPlaceholder();
        tblVentas.getSelectionModel().clearSelection();
        cmbBusqueda.getSelectionModel().clearSelection();
        txtBusqueda.clear();
    }
    @FXML
    private void onGuardar() {
        var expendio = cboExpendio.getSelectionModel().getSelectedItem();
        var present = cboPresentacion.getSelectionModel().getSelectedItem();
        var fecha = dpFecha.getValue();
        if (expendio == null || present == null || fecha == null || txtCantidad.getText().isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Completa expendio, presentación, fecha y cantidad").showAndWait();
            return;
        }
        int cantidad = Integer.parseInt(txtCantidad.getText());

        if (seleccionado == null) {
            dao.insert(new Venta(null, expendio.id, present.id, fecha, cantidad));
        } else {
            seleccionado.setIdexpendio(expendio.id);
            seleccionado.setIdpresentacion(present.id);
            seleccionado.setFecha(fecha);
            seleccionado.setCantidad(cantidad);
            dao.update(seleccionado);
        }
        refrescarTabla();
        onNuevo();
    }
    @FXML
    private void onActualizar() {
        Venta sel = tblVentas.getSelectionModel().getSelectedItem();
        if (sel == null) { info("Selecciona una venta para actualizar", ""); return; }
        var expendio = cboExpendio.getSelectionModel().getSelectedItem();
        var present = cboPresentacion.getSelectionModel().getSelectedItem();
        var fecha = dpFecha.getValue();
        var cantidadStr = txtCantidad.getText();
        if (expendio == null || present == null || fecha == null || cantidadStr.isBlank()) {
            info("Completa expendio, presentación, fecha y cantidad", "");
            return;
        }
        int cantidad = Integer.parseInt(cantidadStr);
        boolean hayCambios =
            expendio.id != sel.getIdexpendio() ||
            present.id != sel.getIdpresentacion() ||
            !fecha.equals(sel.getFecha()) ||
            cantidad != sel.getCantidad();
        if (!hayCambios) {
            info("Sin cambios", "No hay cambios por guardar.");
            return;
        }
        sel.setIdexpendio(expendio.id);
        sel.setIdpresentacion(present.id);
        sel.setFecha(fecha);
        sel.setCantidad(cantidad);
        dao.update(sel);
        refrescarTabla();
        onNuevo();
        info("Actualizado", "Venta actualizada correctamente");
    }
    @FXML
    private void onEliminar() {
        Venta v = tblVentas.getSelectionModel().getSelectedItem();
        if (v == null) return;
        if (new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar venta " + v.getIdventa() + "?", ButtonType.OK, ButtonType.CANCEL)
                .showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            dao.delete(v.getIdventa());
            refrescarTabla();
            onNuevo();
        }
    }
    private void info(String h, String m) { Alert a = new Alert(Alert.AlertType.INFORMATION); a.setHeaderText(h); a.setContentText(m); a.showAndWait(); }

    public static class ComboItem {
        public final int id; public final String nombre;
        public ComboItem(int id, String nombre) { this.id = id; this.nombre = nombre; }
        @Override public String toString() { return nombre + " (ID " + id + ")"; }
    }

    private void actualizarPlaceholder() {
        if (!ventasEnTabla.isEmpty()) {
            tblVentas.setPlaceholder(new Label(" "));
        } else if (listaCompletaCargada) {
            tblVentas.setPlaceholder(new Label("No hay registros disponibles"));
        } else {
            tblVentas.setPlaceholder(new Label("Realiza una búsqueda para ver resultados"));
        }
    }
}
