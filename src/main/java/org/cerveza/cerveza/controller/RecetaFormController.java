package org.cerveza.cerveza.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.cerveza.cerveza.dao.impl.RecetaDaoImpl;
import org.cerveza.cerveza.model.Receta;

import java.sql.*;

import static org.cerveza.cerveza.config.Database.getConnection;

public class RecetaFormController {

    @FXML private ComboBox<ComboItem> cboCerveza;
    @FXML private ComboBox<ComboItem> cboIngrediente;
    @FXML private TextField txtCantidad;

    @FXML private TableView<Receta> tblRecetas;
    @FXML private TableColumn<Receta, Integer> colId;
    @FXML private TableColumn<Receta, Integer> colCerveza;
    @FXML private TableColumn<Receta, Integer> colIngrediente;
    @FXML private TableColumn<Receta, Integer> colCantidad;

    @FXML private ComboBox<String> cmbBusqueda;
    @FXML private TextField txtBusqueda;
    @FXML private Button btnActualizar;
    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;

    private final RecetaDaoImpl dao = new RecetaDaoImpl();
    private Receta seleccionado;
    private ObservableList<Receta> todasLasRecetas = FXCollections.observableArrayList();
    private final ObservableList<Receta> recetasEnTabla = FXCollections.observableArrayList();
    private boolean listaCompletaCargada = false;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idreceta"));
        colCerveza.setCellValueFactory(new PropertyValueFactory<>("idcerveza"));
        colIngrediente.setCellValueFactory(new PropertyValueFactory<>("idingrediente"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        cargarCombos();

        // Buscador
        cmbBusqueda.setItems(FXCollections.observableArrayList("ID", "Cerveza", "Ingrediente", "Cantidad"));
        cmbBusqueda.setPromptText("Elige un campo...");
        cmbBusqueda.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltroBusqueda());
        txtBusqueda.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltroBusqueda());

        // Estado inicial de botones
        btnActualizar.setDisable(true);
        btnGuardar.setDisable(false);
        btnEliminar.setDisable(true);

        // No cargar registros al iniciar
        tblRecetas.setItems(recetasEnTabla);
        actualizarPlaceholder();

        // Selección en tabla
        tblRecetas.getSelectionModel().selectedItemProperty().addListener((obs, a, b) -> {
            seleccionado = b;
            if (b != null) {
                selectComboById(cboCerveza, b.getIdcerveza());
                selectComboById(cboIngrediente, b.getIdingrediente());
                txtCantidad.setText(b.getCantidad() == null ? "" : String.valueOf(b.getCantidad()));
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
        cboCerveza.setItems(FXCollections.observableArrayList(consultarIdNombre("cerveza")));
        cboIngrediente.setItems(FXCollections.observableArrayList(consultarIdNombre("ingrediente")));
    }

    private ObservableList<ComboItem> consultarIdNombre(String tabla) {
        ObservableList<ComboItem> data = FXCollections.observableArrayList();
        String sql = "SELECT " + (tabla.equals("cerveza") ? "idcerveza" : "idingrediente") + " AS id, nombre FROM " + tabla + " ORDER BY nombre";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) data.add(new ComboItem(rs.getInt("id"), rs.getString("nombre")));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    private void selectComboById(ComboBox<ComboItem> combo, Integer id) {
        if (id == null) return;
        for (ComboItem item : combo.getItems()) {
            if (item.id == id) { combo.getSelectionModel().select(item); return; }
        }
    }

    private void refrescarTabla() {
        todasLasRecetas.setAll(dao.findAll());
        if (listaCompletaCargada) {
            recetasEnTabla.setAll(todasLasRecetas);
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
                recetasEnTabla.setAll(todasLasRecetas);
                actualizarPlaceholder();
            } else {
                recetasEnTabla.clear();
                actualizarPlaceholder();
            }
            return;
        }
        if (todasLasRecetas.isEmpty()) {
            todasLasRecetas.setAll(dao.findAll());
        }
        ObservableList<Receta> resultados = FXCollections.observableArrayList();
        for (Receta r : todasLasRecetas) {
            switch (campo) {
                case "ID":
                    if (String.valueOf(r.getIdreceta()).contains(texto)) resultados.add(r);
                    break;
                case "Cerveza":
                    if (String.valueOf(r.getIdcerveza()).contains(texto)) resultados.add(r);
                    break;
                case "Ingrediente":
                    if (String.valueOf(r.getIdingrediente()).contains(texto)) resultados.add(r);
                    break;
                case "Cantidad":
                    if (r.getCantidad() != null && String.valueOf(r.getCantidad()).contains(texto)) resultados.add(r);
                    break;
            }
        }
        recetasEnTabla.setAll(resultados);
        if (recetasEnTabla.isEmpty()) {
            tblRecetas.setPlaceholder(new Label("No se encontró en la base de datos"));
        } else {
            tblRecetas.setPlaceholder(new Label(" "));
        }
    }

    @FXML
    private void onNuevo() {
        tblRecetas.getSelectionModel().clearSelection();
        seleccionado = null;
        cboCerveza.getSelectionModel().clearSelection();
        cboIngrediente.getSelectionModel().clearSelection();
        txtCantidad.clear();
        txtBusqueda.clear();
        cmbBusqueda.getSelectionModel().clearSelection();
        btnActualizar.setDisable(true);
        btnGuardar.setDisable(false);
        btnEliminar.setDisable(true);
    }

    @FXML
    private void onListar() {
        todasLasRecetas.setAll(dao.findAll());
        listaCompletaCargada = true;
        recetasEnTabla.setAll(todasLasRecetas);
        actualizarPlaceholder();
        tblRecetas.getSelectionModel().clearSelection();
        cmbBusqueda.getSelectionModel().clearSelection();
        txtBusqueda.clear();
    }

    @FXML
    private void onGuardar() {
        ComboItem cerveza = cboCerveza.getSelectionModel().getSelectedItem();
        ComboItem ing = cboIngrediente.getSelectionModel().getSelectedItem();
        if (cerveza == null || ing == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona cerveza e ingrediente").showAndWait();
            return;
        }
        Integer cantidad = txtCantidad.getText().isBlank() ? null : Integer.parseInt(txtCantidad.getText());

        if (seleccionado == null) {
            Receta r = new Receta(null, cerveza.id, ing.id, cantidad);
            dao.insert(r);
        } else {
            seleccionado.setIdcerveza(cerveza.id);
            seleccionado.setIdingrediente(ing.id);
            seleccionado.setCantidad(cantidad);
            dao.update(seleccionado);
        }
        refrescarTabla();
        onNuevo();
    }

    @FXML
    private void onActualizar() {
        Receta sel = tblRecetas.getSelectionModel().getSelectedItem();
        if (sel == null) { info("Selecciona una receta para actualizar", ""); return; }
        ComboItem cerveza = cboCerveza.getSelectionModel().getSelectedItem();
        ComboItem ing = cboIngrediente.getSelectionModel().getSelectedItem();
        Integer cantidad = txtCantidad.getText().isBlank() ? null : Integer.parseInt(txtCantidad.getText());
        boolean hayCambios =
            cerveza.id != sel.getIdcerveza() ||
            ing.id != sel.getIdingrediente() ||
            (cantidad != null && !cantidad.equals(sel.getCantidad()));
        if (!hayCambios) {
            info("Sin cambios", "No hay cambios por guardar.");
            return;
        }
        sel.setIdcerveza(cerveza.id);
        sel.setIdingrediente(ing.id);
        sel.setCantidad(cantidad);
        dao.update(sel);
        refrescarTabla();
        onNuevo();
        info("Actualizado", "Receta actualizada correctamente");
    }

    @FXML
    private void onEliminar() {
        Receta r = tblRecetas.getSelectionModel().getSelectedItem();
        if (r == null) return;
        if (new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar receta " + r.getIdreceta() + "?", ButtonType.OK, ButtonType.CANCEL).showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            dao.delete(r.getIdreceta());
            refrescarTabla();
            onNuevo();
        }
    }

    // Item simple para combos
        public record ComboItem(int id, String nombre) {
        @Override
        public String toString() {
            return nombre + " (ID " + id + ")";
        }
        }

    private void info(String h, String m) { Alert a = new Alert(Alert.AlertType.INFORMATION); a.setHeaderText(h); a.setContentText(m); a.showAndWait(); }

    private void actualizarPlaceholder() {
        if (!recetasEnTabla.isEmpty()) {
            tblRecetas.setPlaceholder(new Label(" "));
        } else if (listaCompletaCargada) {
            tblRecetas.setPlaceholder(new Label("No hay registros disponibles"));
        } else {
            tblRecetas.setPlaceholder(new Label("Realiza una búsqueda para ver resultados"));
        }
    }
}
