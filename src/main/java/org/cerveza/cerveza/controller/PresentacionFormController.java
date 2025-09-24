package org.cerveza.cerveza.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.cerveza.cerveza.dao.PresentacionDao;
import org.cerveza.cerveza.dao.impl.PresentacionDaoImpl;
import org.cerveza.cerveza.model.Presentacion;

import java.sql.*;
import java.util.Optional;

import static org.cerveza.cerveza.config.Database.getConnection;

public class PresentacionFormController {

    @FXML private ComboBox<IdName> cmbEnvase;
    @FXML private ComboBox<IdName> cmbCerveza;
    @FXML private TableView<Presentacion> tblPresentaciones;
    @FXML private TableColumn<Presentacion, Number> colId;
    @FXML private TableColumn<Presentacion, String> colEnvase;
    @FXML private TableColumn<Presentacion, String> colCerveza;
    @FXML private Button btnNuevo, btnGuardar, btnEliminar;
    @FXML private ComboBox<String> cmbBusqueda;
    @FXML private TextField txtBusqueda;
    @FXML private Button btnActualizar;

    private final PresentacionDao dao = new PresentacionDaoImpl();
    private Presentacion seleccionado;
    private ObservableList<Presentacion> todasLasPresentaciones = FXCollections.observableArrayList();
    private final ObservableList<Presentacion> presentacionesEnTabla = FXCollections.observableArrayList();
    private boolean listaCompletaCargada = false;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIdPresentacion()));
        colEnvase.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                Optional.ofNullable(c.getValue().getEnvaseNombre()).orElse("id=" + c.getValue().getIdEnvase())));
        colCerveza.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                Optional.ofNullable(c.getValue().getCervezaNombre()).orElse("id=" + c.getValue().getIdCerveza())));
        cargarCombos();
        // Buscador
        cmbBusqueda.setItems(FXCollections.observableArrayList("ID", "Envase", "Cerveza"));
        cmbBusqueda.setPromptText("Elige un campo...");
        cmbBusqueda.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltroBusqueda());
        txtBusqueda.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltroBusqueda());
        // Estado inicial de botones
        btnActualizar.setDisable(true);
        btnGuardar.setDisable(false);
        btnEliminar.setDisable(true);
        // No cargar registros al iniciar
        tblPresentaciones.setItems(presentacionesEnTabla);
        actualizarPlaceholder();
        // Selección en tabla
        tblPresentaciones.getSelectionModel().selectedItemProperty().addListener((obs, a, b) -> {
            seleccionado = b;
            if (b != null) {
                seleccionarComboPorId(cmbEnvase, b.getIdEnvase());
                seleccionarComboPorId(cmbCerveza, b.getIdCerveza());
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
        cmbEnvase.setItems(cargarIdName("SELECT idenvase, nombre FROM envase ORDER BY nombre", "idenvase", "nombre"));
        cmbCerveza.setItems(cargarIdName("SELECT idcerveza, nombre FROM cerveza ORDER BY nombre", "idcerveza", "nombre"));
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

    private void refrescarTabla() {
        todasLasPresentaciones.setAll(dao.findAllWithLabels());
        if (listaCompletaCargada) {
            presentacionesEnTabla.setAll(todasLasPresentaciones);
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
                presentacionesEnTabla.setAll(todasLasPresentaciones);
                actualizarPlaceholder();
            } else {
                presentacionesEnTabla.clear();
                actualizarPlaceholder();
            }
            return;
        }
        if (todasLasPresentaciones.isEmpty()) {
            todasLasPresentaciones.setAll(dao.findAllWithLabels());
        }
        ObservableList<Presentacion> resultados = FXCollections.observableArrayList();
        for (Presentacion p : todasLasPresentaciones) {
            switch (campo) {
                case "ID":
                    if (String.valueOf(p.getIdPresentacion()).contains(texto)) resultados.add(p);
                    break;
                case "Envase":
                    if (p.getEnvaseNombre() != null && p.getEnvaseNombre().toLowerCase().contains(texto.toLowerCase())) resultados.add(p);
                    break;
                case "Cerveza":
                    if (p.getCervezaNombre() != null && p.getCervezaNombre().toLowerCase().contains(texto.toLowerCase())) resultados.add(p);
                    break;
            }
        }
        presentacionesEnTabla.setAll(resultados);
        if (presentacionesEnTabla.isEmpty()) {
            tblPresentaciones.setPlaceholder(new Label("No se encontró en la base de datos"));
        } else {
            tblPresentaciones.setPlaceholder(new Label(" "));
        }
    }
    @FXML
    private void onNuevo() {
        tblPresentaciones.getSelectionModel().clearSelection();
        cmbEnvase.getSelectionModel().clearSelection();
        cmbCerveza.getSelectionModel().clearSelection();
        seleccionado = null;
        txtBusqueda.clear();
        cmbBusqueda.getSelectionModel().clearSelection();
        btnActualizar.setDisable(true);
        btnGuardar.setDisable(false);
        btnEliminar.setDisable(true);
    }
    @FXML
    private void onListar() {
        todasLasPresentaciones.setAll(dao.findAllWithLabels());
        listaCompletaCargada = true;
        presentacionesEnTabla.setAll(todasLasPresentaciones);
        actualizarPlaceholder();
        tblPresentaciones.getSelectionModel().clearSelection();
        cmbBusqueda.getSelectionModel().clearSelection();
        txtBusqueda.clear();
    }

    @FXML
    private void onGuardar() {
        IdName env = cmbEnvase.getValue();
        IdName cz = cmbCerveza.getValue();
        if (env == null || cz == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona envase y cerveza.").showAndWait();
            return;
        }

        if (seleccionado == null) {
            Presentacion p = new Presentacion(0, env.id(), cz.id(), env.name(), cz.name());
            int id = dao.insert(p);
            if (id > 0) refrescarTabla();
        } else {
            seleccionado.setIdEnvase(env.id());
            seleccionado.setIdCerveza(cz.id());
            if (dao.update(seleccionado)) refrescarTabla();
        }
    }

    @FXML
    private void onActualizar() {
        Presentacion sel = tblPresentaciones.getSelectionModel().getSelectedItem();
        if (sel == null) { info("Selecciona una presentación para actualizar", ""); return; }
        IdName env = cmbEnvase.getValue();
        IdName cz = cmbCerveza.getValue();
        if (env == null || cz == null) {
            info("Selecciona envase y cerveza.", "");
            return;
        }
        boolean hayCambios = env.id() != sel.getIdEnvase() || cz.id() != sel.getIdCerveza();
        if (!hayCambios) {
            info("Sin cambios", "No hay cambios por guardar.");
            return;
        }
        sel.setIdEnvase(env.id());
        sel.setIdCerveza(cz.id());
        if (dao.update(sel)) {
            refrescarTabla();
            info("Actualizado", "Presentación actualizada correctamente");
        }
        onNuevo();
    }

    @FXML
    private void onEliminar() {
        Presentacion row = tblPresentaciones.getSelectionModel().getSelectedItem();
        if (row == null) return;
        if (confirm("¿Eliminar la presentación #" + row.getIdPresentacion() + "?")) {
            if (dao.delete(row.getIdPresentacion())) refrescarTabla();
        }
    }

    private boolean confirm(String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL);
        return a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    // pequeño record para combos
    public record IdName(int id, String name) { @Override public String toString(){ return name; } }

    private void actualizarPlaceholder() {
        if (!presentacionesEnTabla.isEmpty()) {
            tblPresentaciones.setPlaceholder(new Label(" "));
        } else if (listaCompletaCargada) {
            tblPresentaciones.setPlaceholder(new Label("No hay registros disponibles"));
        } else {
            tblPresentaciones.setPlaceholder(new Label("Realiza una búsqueda para ver resultados"));
        }
    }

    private void info(String h, String m) { Alert a = new Alert(Alert.AlertType.INFORMATION); a.setHeaderText(h); a.setContentText(m); a.showAndWait(); }
}
