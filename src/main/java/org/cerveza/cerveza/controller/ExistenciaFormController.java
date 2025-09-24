package org.cerveza.cerveza.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.cerveza.cerveza.dao.ExistenciaDao;
import org.cerveza.cerveza.dao.impl.ExistenciaDaoImpl;
import org.cerveza.cerveza.model.Existencia;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

import static org.cerveza.cerveza.config.Database.getConnection;

public class ExistenciaFormController {

    @FXML private ComboBox<IdName> cmbExpendio;
    @FXML private ComboBox<IdName> cmbPresentacion;
    @FXML private TextField txtCantidad;
    @FXML private DatePicker dpFecha;

    @FXML private TableView<Existencia> tblExistencias;
    @FXML private TableColumn<Existencia, Number> colId;
    @FXML private TableColumn<Existencia, String> colExpendio;
    @FXML private TableColumn<Existencia, String> colPresentacion;
    @FXML private TableColumn<Existencia, Number> colCantidad;
    @FXML private TableColumn<Existencia, String> colFecha;

    @FXML private ComboBox<String> cmbBusqueda;
    @FXML private TextField txtBusqueda;
    @FXML private Button btnActualizar;
    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;

    private final ExistenciaDao dao = new ExistenciaDaoImpl();
    private Existencia seleccionado;
    private ObservableList<Existencia> todasLasExistencias = FXCollections.observableArrayList();
    private final ObservableList<Existencia> existenciasEnTabla = FXCollections.observableArrayList();
    private boolean listaCompletaCargada = false;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIdExistencia()));
        colExpendio.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                Optional.ofNullable(c.getValue().getExpendioNombre()).orElse("id=" + c.getValue().getIdExpendio())));
        colPresentacion.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                Optional.ofNullable(c.getValue().getPresentacionNombre()).orElse("id=" + c.getValue().getIdPresentacion())));
        colCantidad.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getCantidad()));
        colFecha.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getFecha().toString()));

        cargarCombos();
        // Buscador
        cmbBusqueda.setItems(FXCollections.observableArrayList("ID", "Expendio", "Presentación", "Cantidad", "Fecha"));
        cmbBusqueda.setPromptText("Elige un campo...");
        cmbBusqueda.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltroBusqueda());
        txtBusqueda.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltroBusqueda());
        // Estado inicial de botones
        btnActualizar.setDisable(true);
        btnGuardar.setDisable(false);
        btnEliminar.setDisable(true);
        // No cargar registros al iniciar
        tblExistencias.setItems(existenciasEnTabla);
        actualizarPlaceholder();
        // Selección en tabla
        tblExistencias.getSelectionModel().selectedItemProperty().addListener((obs, a, b) -> {
            seleccionado = b;
            if (b != null) {
                seleccionarComboPorId(cmbExpendio, b.getIdExpendio());
                seleccionarComboPorId(cmbPresentacion, b.getIdPresentacion());
                txtCantidad.setText(Integer.toString(b.getCantidad()));
                dpFecha.setValue(b.getFecha());
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
        cmbExpendio.setItems(cargarIdName("SELECT idexpendio, nombre FROM expendio ORDER BY nombre", "idexpendio", "nombre"));
        cmbPresentacion.setItems(cargarIdName("""
            SELECT p.idpresentacion,
                   CONCAT(e.nombre,' - ',c.nombre) AS nom
            FROM presentacion p
            JOIN envase e ON e.idenvase=p.idenvase
            JOIN cerveza c ON c.idcerveza=p.idcerveza
            ORDER BY nom
        """, "idpresentacion", "nom"));
    }

    private ObservableList<IdName> cargarIdName(String sql, String idCol, String nameCol) {
        ObservableList<IdName> data = FXCollections.observableArrayList();
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) data.add(new IdName(rs.getInt(idCol), rs.getString(nameCol)));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    private void seleccionarComboPorId(ComboBox<IdName> combo, int id) {
        for (IdName i : combo.getItems()) if (i.id() == id) { combo.getSelectionModel().select(i); break; }
    }

    @FXML
    private void onNuevo() { limpiar(); }

    @FXML
    private void onGuardar() {
        IdName exp = cmbExpendio.getValue();
        IdName pre = cmbPresentacion.getValue();
        String cantStr = txtCantidad.getText();
        LocalDate fecha = dpFecha.getValue();

        if (exp == null || pre == null || cantStr == null || cantStr.isBlank() || fecha == null) {
            new Alert(Alert.AlertType.WARNING, "Completa expendio, presentación, cantidad y fecha.").showAndWait();
            return;
        }
        int cantidad;
        try { cantidad = Integer.parseInt(cantStr); }
        catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.WARNING, "Cantidad debe ser numérica.").showAndWait(); return;
        }

        if (seleccionado == null) {
            Existencia e = new Existencia(0, exp.id(), pre.id(), cantidad, fecha, exp.name(), pre.name());
            int id = dao.insert(e);
            if (id > 0) refrescarTabla();
        } else {
            seleccionado.setIdExpendio(exp.id());
            seleccionado.setIdPresentacion(pre.id());
            seleccionado.setCantidad(cantidad);
            seleccionado.setFecha(fecha);
            if (dao.update(seleccionado)) refrescarTabla();
        }
    }

    @FXML
    private void onActualizar() {
        Existencia sel = tblExistencias.getSelectionModel().getSelectedItem();
        if (sel == null) { error("Selecciona una existencia para actualizar"); return; }
        try {
            IdName exp = cmbExpendio.getValue();
            IdName pre = cmbPresentacion.getValue();
            String cantStr = txtCantidad.getText();
            LocalDate fecha = dpFecha.getValue();
            if (exp == null || pre == null || cantStr == null || cantStr.isBlank() || fecha == null) {
                error("Completa expendio, presentación, cantidad y fecha.");
                return;
            }
            int cantidad = Integer.parseInt(cantStr);
            boolean hayCambios =
                exp.id() != sel.getIdExpendio() ||
                pre.id() != sel.getIdPresentacion() ||
                cantidad != sel.getCantidad() ||
                !fecha.equals(sel.getFecha());
            if (!hayCambios) {
                info("Sin cambios", "No hay cambios por guardar.");
                return;
            }
            sel.setIdExpendio(exp.id());
            sel.setIdPresentacion(pre.id());
            sel.setCantidad(cantidad);
            sel.setFecha(fecha);
            if (dao.update(sel)) {
                refrescarTabla();
                info("Actualizado", "Existencia actualizada correctamente");
            }
            limpiar();
        } catch (NumberFormatException ex) {
            error("Cantidad debe ser numérica.");
        } catch (Exception ex) {
            error("Error al actualizar: " + ex.getMessage());
        }
    }

    @FXML
    private void onEliminar() {
        Existencia row = tblExistencias.getSelectionModel().getSelectedItem();
        if (row == null) return;
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar la existencia #" + row.getIdExistencia() + "?", ButtonType.OK, ButtonType.CANCEL);
        if (a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                dao.delete(row.getIdExistencia());
                refrescarTabla();
                limpiar();
                info("Eliminado", "Envase eliminado correctamente");
            } catch (Exception e) { error("No se pudo eliminar: " + e.getMessage()); }
        }
    }

    private void info(String h, String m) { Alert a = new Alert(Alert.AlertType.INFORMATION); a.setHeaderText(h); a.setContentText(m); a.showAndWait(); }
    private void error(String m) { Alert a = new Alert(Alert.AlertType.ERROR); a.setHeaderText("Validación"); a.setContentText(m); a.showAndWait(); }
    private void refrescarTabla() {
        todasLasExistencias.setAll(dao.findAllWithLabels());
        if (listaCompletaCargada) {
            existenciasEnTabla.setAll(todasLasExistencias);
            actualizarPlaceholder();
        }
        aplicarFiltroBusqueda();
        limpiar();
    }
    private void aplicarFiltroBusqueda() {
        String campo = cmbBusqueda.getValue();
        String texto = txtBusqueda.getText();
        if (campo == null || texto == null || texto.isBlank()) {
            if (listaCompletaCargada) {
                existenciasEnTabla.setAll(todasLasExistencias);
                actualizarPlaceholder();
            } else {
                existenciasEnTabla.clear();
                actualizarPlaceholder();
            }
            return;
        }
        if (todasLasExistencias.isEmpty()) {
            todasLasExistencias.setAll(dao.findAllWithLabels());
        }
        ObservableList<Existencia> resultados = FXCollections.observableArrayList();
        for (Existencia e : todasLasExistencias) {
            switch (campo) {
                case "ID":
                    if (String.valueOf(e.getIdExistencia()).contains(texto)) resultados.add(e);
                    break;
                case "Expendio":
                    if (e.getExpendioNombre() != null && e.getExpendioNombre().toLowerCase().contains(texto.toLowerCase())) resultados.add(e);
                    break;
                case "Presentación":
                    if (e.getPresentacionNombre() != null && e.getPresentacionNombre().toLowerCase().contains(texto.toLowerCase())) resultados.add(e);
                    break;
                case "Cantidad":
                    if (String.valueOf(e.getCantidad()).contains(texto)) resultados.add(e);
                    break;
                case "Fecha":
                    if (e.getFecha() != null && e.getFecha().toString().contains(texto)) resultados.add(e);
                    break;
            }
        }
        existenciasEnTabla.setAll(resultados);
        if (existenciasEnTabla.isEmpty()) {
            tblExistencias.setPlaceholder(new Label("No se encontró en la base de datos"));
        } else {
            tblExistencias.setPlaceholder(new Label(" "));
        }
    }
    private void limpiar() {
        tblExistencias.getSelectionModel().clearSelection();
        cmbExpendio.getSelectionModel().clearSelection();
        cmbPresentacion.getSelectionModel().clearSelection();
        txtCantidad.clear();
        dpFecha.setValue(null);
        seleccionado = null;
        txtBusqueda.clear();
        cmbBusqueda.getSelectionModel().clearSelection();
        btnActualizar.setDisable(true);
        btnGuardar.setDisable(false);
        btnEliminar.setDisable(true);
    }
    @FXML
    private void onListar() {
        todasLasExistencias.setAll(dao.findAllWithLabels());
        listaCompletaCargada = true;
        existenciasEnTabla.setAll(todasLasExistencias);
        actualizarPlaceholder();
        tblExistencias.getSelectionModel().clearSelection();
        cmbBusqueda.getSelectionModel().clearSelection();
        txtBusqueda.clear();
    }
    // record simple para combos
    public record IdName(int id, String name) { @Override public String toString(){ return name; } }

    private void actualizarPlaceholder() {
        if (!existenciasEnTabla.isEmpty()) {
            tblExistencias.setPlaceholder(new Label(" "));
        } else if (listaCompletaCargada) {
            tblExistencias.setPlaceholder(new Label("No hay registros disponibles"));
        } else {
            tblExistencias.setPlaceholder(new Label("Realiza una búsqueda para ver resultados"));
        }
    }
}
