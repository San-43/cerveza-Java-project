package org.cerveza.cerveza.controller;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.cerveza.cerveza.dao.CervezaDao;
import org.cerveza.cerveza.dao.ExistenciaDao;
import org.cerveza.cerveza.dao.impl.CervezaDaoImpl;
import org.cerveza.cerveza.dao.impl.ExistenciaDaoImpl;
import org.cerveza.cerveza.model.Cerveza;
import org.cerveza.cerveza.model.Existencia;

import java.time.LocalDate;
import java.util.function.UnaryOperator;

public class SearchController {

    // --- Cerveza ---
    @FXML private TextField txtCervezaId;
    @FXML private TextField txtCervezaIdMarca;
    @FXML private TextField txtCervezaNombre;
    @FXML private TextField txtCervezaAspecto;
    @FXML private TextField txtCervezaProcedimientos;
    @FXML private TextField txtCervezaGradMin;
    @FXML private TextField txtCervezaGradMax;
    @FXML private TextField txtCervezaExistMin;
    @FXML private TextField txtCervezaExistMax;
    @FXML private TableView<Cerveza> tblCervezas;
    @FXML private TableColumn<Cerveza, Integer> colCerId;
    @FXML private TableColumn<Cerveza, Integer> colCerIdMarca;
    @FXML private TableColumn<Cerveza, String> colCerNombre;
    @FXML private TableColumn<Cerveza, String> colCerAspecto;
    @FXML private TableColumn<Cerveza, String> colCerProcedimientos;
    @FXML private TableColumn<Cerveza, Double> colCerGraduacion;
    @FXML private TableColumn<Cerveza, Integer> colCerExistTotal;

    // --- Existencia ---
    @FXML private TextField txtExistId;
    @FXML private TextField txtExistIdExpendio;
    @FXML private TextField txtExistNombreExpendio;
    @FXML private TextField txtExistIdPresentacion;
    @FXML private TextField txtExistNombrePresentacion;
    @FXML private TextField txtExistCantidadMin;
    @FXML private TextField txtExistCantidadMax;
    @FXML private DatePicker dpExistFechaDesde;
    @FXML private DatePicker dpExistFechaHasta;
    @FXML private TableView<Existencia> tblExistencias;
    @FXML private TableColumn<Existencia, Integer> colExistId;
    @FXML private TableColumn<Existencia, Integer> colExistIdExpendio;
    @FXML private TableColumn<Existencia, String> colExistNombreExpendio;
    @FXML private TableColumn<Existencia, Integer> colExistIdPresentacion;
    @FXML private TableColumn<Existencia, String> colExistNombrePresentacion;
    @FXML private TableColumn<Existencia, Integer> colExistCantidad;
    @FXML private TableColumn<Existencia, LocalDate> colExistFecha;

    private final CervezaDao cervezaDao = new CervezaDaoImpl();
    private final ExistenciaDao existenciaDao = new ExistenciaDaoImpl();

    private final ObservableList<Cerveza> cervezaMaster = FXCollections.observableArrayList();
    private final FilteredList<Cerveza> cervezaFiltered = new FilteredList<>(cervezaMaster, c -> true);

    private final ObservableList<Existencia> existenciaMaster = FXCollections.observableArrayList();
    private final FilteredList<Existencia> existenciaFiltered = new FilteredList<>(existenciaMaster, e -> true);

    @FXML
    public void initialize() {
        configureNumericTextField(txtCervezaId, false);
        configureNumericTextField(txtCervezaIdMarca, false);
        configureNumericTextField(txtCervezaGradMin, true);
        configureNumericTextField(txtCervezaGradMax, true);
        configureNumericTextField(txtCervezaExistMin, false);
        configureNumericTextField(txtCervezaExistMax, false);

        configureNumericTextField(txtExistId, false);
        configureNumericTextField(txtExistIdExpendio, false);
        configureNumericTextField(txtExistIdPresentacion, false);
        configureNumericTextField(txtExistCantidadMin, false);
        configureNumericTextField(txtExistCantidadMax, false);

        setupCervezaTable();
        setupExistenciaTable();

        loadCervezaData();
        loadExistenciaData();

        registerCervezaFilterListeners();
        registerExistenciaFilterListeners();
    }

    private void setupCervezaTable() {
        colCerId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCerIdMarca.setCellValueFactory(new PropertyValueFactory<>("idMarca"));
        colCerNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCerAspecto.setCellValueFactory(new PropertyValueFactory<>("aspecto"));
        colCerProcedimientos.setCellValueFactory(new PropertyValueFactory<>("procedimientos"));
        colCerGraduacion.setCellValueFactory(new PropertyValueFactory<>("graduacion"));
        colCerExistTotal.setCellValueFactory(new PropertyValueFactory<>("existenciaTotal"));
        tblCervezas.setItems(cervezaFiltered);
        tblCervezas.setPlaceholder(new Label("No hay resultados para mostrar"));
    }

    private void setupExistenciaTable() {
        colExistId.setCellValueFactory(new PropertyValueFactory<>("idExistencia"));
        colExistIdExpendio.setCellValueFactory(new PropertyValueFactory<>("idExpendio"));
        colExistNombreExpendio.setCellValueFactory(new PropertyValueFactory<>("expendioNombre"));
        colExistIdPresentacion.setCellValueFactory(new PropertyValueFactory<>("idPresentacion"));
        colExistNombrePresentacion.setCellValueFactory(new PropertyValueFactory<>("presentacionNombre"));
        colExistCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colExistFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        tblExistencias.setItems(existenciaFiltered);
        tblExistencias.setPlaceholder(new Label("No hay resultados para mostrar"));
    }

    private void registerCervezaFilterListeners() {
        ChangeListener<String> listener = (obs, oldValue, newValue) -> updateCervezaFilter();
        txtCervezaId.textProperty().addListener(listener);
        txtCervezaIdMarca.textProperty().addListener(listener);
        txtCervezaNombre.textProperty().addListener(listener);
        txtCervezaAspecto.textProperty().addListener(listener);
        txtCervezaProcedimientos.textProperty().addListener(listener);
        txtCervezaGradMin.textProperty().addListener(listener);
        txtCervezaGradMax.textProperty().addListener(listener);
        txtCervezaExistMin.textProperty().addListener(listener);
        txtCervezaExistMax.textProperty().addListener(listener);
    }

    private void registerExistenciaFilterListeners() {
        ChangeListener<String> listener = (obs, oldValue, newValue) -> updateExistenciaFilter();
        txtExistId.textProperty().addListener(listener);
        txtExistIdExpendio.textProperty().addListener(listener);
        txtExistNombreExpendio.textProperty().addListener(listener);
        txtExistIdPresentacion.textProperty().addListener(listener);
        txtExistNombrePresentacion.textProperty().addListener(listener);
        txtExistCantidadMin.textProperty().addListener(listener);
        txtExistCantidadMax.textProperty().addListener(listener);
        dpExistFechaDesde.valueProperty().addListener((obs, oldValue, newValue) -> updateExistenciaFilter());
        dpExistFechaHasta.valueProperty().addListener((obs, oldValue, newValue) -> updateExistenciaFilter());
    }

    private void configureNumericTextField(TextField field, boolean allowDecimal) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) return change;
            String pattern = allowDecimal ? "-?\\d*(?:[.]\\d*)?" : "-?\\d*";
            return newText.matches(pattern) ? change : null;
        };
        field.setTextFormatter(new TextFormatter<>(filter));
    }

    private void loadCervezaData() {
        cervezaMaster.setAll(cervezaDao.findAll());
        updateCervezaFilter();
    }

    private void loadExistenciaData() {
        existenciaMaster.setAll(existenciaDao.findAllWithLabels());
        updateExistenciaFilter();
    }

    private void updateCervezaFilter() {
        cervezaFiltered.setPredicate(cerveza -> {
            if (cerveza == null) return false;

            Integer idFilter = parseInteger(txtCervezaId);
            if (idFilter != null && !idFilter.equals(cerveza.getId())) return false;

            Integer idMarcaFilter = parseInteger(txtCervezaIdMarca);
            if (idMarcaFilter != null && !idMarcaFilter.equals(cerveza.getIdMarca())) return false;

            String nombreFilter = normalize(txtCervezaNombre.getText());
            if (!nombreFilter.isEmpty() && !containsIgnoreCase(cerveza.getNombre(), nombreFilter)) return false;

            String aspectoFilter = normalize(txtCervezaAspecto.getText());
            if (!aspectoFilter.isEmpty() && !containsIgnoreCase(cerveza.getAspecto(), aspectoFilter)) return false;

            String procFilter = normalize(txtCervezaProcedimientos.getText());
            if (!procFilter.isEmpty() && !containsIgnoreCase(cerveza.getProcedimientos(), procFilter)) return false;

            Double gradMin = parseDouble(txtCervezaGradMin);
            if (gradMin != null) {
                Double grad = cerveza.getGraduacion();
                if (grad == null || grad < gradMin) return false;
            }

            Double gradMax = parseDouble(txtCervezaGradMax);
            if (gradMax != null) {
                Double grad = cerveza.getGraduacion();
                if (grad == null || grad > gradMax) return false;
            }

            Integer existMin = parseInteger(txtCervezaExistMin);
            if (existMin != null) {
                Integer total = cerveza.getExistenciaTotal();
                if (total == null || total < existMin) return false;
            }

            Integer existMax = parseInteger(txtCervezaExistMax);
            if (existMax != null) {
                Integer total = cerveza.getExistenciaTotal();
                if (total == null || total > existMax) return false;
            }

            return true;
        });
    }

    private void updateExistenciaFilter() {
        existenciaFiltered.setPredicate(existencia -> {
            if (existencia == null) return false;

            Integer idFilter = parseInteger(txtExistId);
            if (idFilter != null && existencia.getIdExistencia() != idFilter) return false;

            Integer idExpFilter = parseInteger(txtExistIdExpendio);
            if (idExpFilter != null && existencia.getIdExpendio() != idExpFilter) return false;

            String expNombreFilter = normalize(txtExistNombreExpendio.getText());
            if (!expNombreFilter.isEmpty() && !containsIgnoreCase(existencia.getExpendioNombre(), expNombreFilter)) return false;

            Integer idPresFilter = parseInteger(txtExistIdPresentacion);
            if (idPresFilter != null && existencia.getIdPresentacion() != idPresFilter) return false;

            String presNombreFilter = normalize(txtExistNombrePresentacion.getText());
            if (!presNombreFilter.isEmpty() && !containsIgnoreCase(existencia.getPresentacionNombre(), presNombreFilter)) return false;

            Integer cantidadMin = parseInteger(txtExistCantidadMin);
            if (cantidadMin != null && existencia.getCantidad() < cantidadMin) return false;

            Integer cantidadMax = parseInteger(txtExistCantidadMax);
            if (cantidadMax != null && existencia.getCantidad() > cantidadMax) return false;

            LocalDate desde = dpExistFechaDesde.getValue();
            if (desde != null && existencia.getFecha().isBefore(desde)) return false;

            LocalDate hasta = dpExistFechaHasta.getValue();
            if (hasta != null && existencia.getFecha().isAfter(hasta)) return false;

            return true;
        });
    }

    private Integer parseInteger(TextField field) {
        String value = field.getText();
        if (value == null || value.isBlank()) return null;
        return Integer.parseInt(value);
    }

    private Double parseDouble(TextField field) {
        String value = field.getText();
        if (value == null || value.isBlank()) return null;
        return Double.parseDouble(value);
    }

    private String normalize(String text) {
        return text == null ? "" : text.trim().toLowerCase();
    }

    private boolean containsIgnoreCase(String source, String filter) {
        return source != null && source.toLowerCase().contains(filter);
    }

    @FXML
    private void onLimpiarFiltrosCerveza() {
        txtCervezaId.clear();
        txtCervezaIdMarca.clear();
        txtCervezaNombre.clear();
        txtCervezaAspecto.clear();
        txtCervezaProcedimientos.clear();
        txtCervezaGradMin.clear();
        txtCervezaGradMax.clear();
        txtCervezaExistMin.clear();
        txtCervezaExistMax.clear();
        updateCervezaFilter();
    }

    @FXML
    private void onLimpiarFiltrosExistencia() {
        txtExistId.clear();
        txtExistIdExpendio.clear();
        txtExistNombreExpendio.clear();
        txtExistIdPresentacion.clear();
        txtExistNombrePresentacion.clear();
        txtExistCantidadMin.clear();
        txtExistCantidadMax.clear();
        dpExistFechaDesde.setValue(null);
        dpExistFechaHasta.setValue(null);
        updateExistenciaFilter();
    }

    @FXML
    private void onRecargarCervezas() {
        loadCervezaData();
    }

    @FXML
    private void onRecargarExistencias() {
        loadExistenciaData();
    }
}
