package org.cerveza.cerveza.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.cerveza.cerveza.dao.impl.FabricanteDaoImpl;
import org.cerveza.cerveza.model.Fabricante;

public class FabricanteFormController {
    @FXML private TableView<Fabricante> fabricanteTable;
    @FXML private TableColumn<Fabricante, Integer> colId;
    @FXML private TableColumn<Fabricante, String> colNombre;
    @FXML private TableColumn<Fabricante, String> colPais;
    @FXML private TableColumn<Fabricante, String> colDescripcion;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPais;
    @FXML private TextField txtDescripcion;
    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;
    @FXML private Label lblError;

    private final FabricanteDaoImpl dao = new FabricanteDaoImpl();
    private ObservableList<Fabricante> fabricantes;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getIdFabricante()).asObject());
        colNombre.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getNombre()));
        colPais.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getPais()));
        colDescripcion.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDescripcion()));
        cargarFabricantes();
        fabricanteTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            mostrarFabricante(newSel);
            btnGuardar.setDisable(newSel != null);
        });
        btnGuardar.setDisable(false); // Ensure enabled on startup
    }

    private void cargarFabricantes() {
        fabricantes = FXCollections.observableArrayList(dao.obtenerTodos());
        fabricanteTable.setItems(fabricantes);
    }

    private void mostrarFabricante(Fabricante fabricante) {
        if (fabricante != null) {
            txtNombre.setText(fabricante.getNombre());
            txtPais.setText(fabricante.getPais());
            txtDescripcion.setText(fabricante.getDescripcion());
        } else {
            txtNombre.clear();
            txtPais.clear();
            txtDescripcion.clear();
        }
    }

    private boolean validarCampos() {
        String nombre = txtNombre.getText();
        String pais = txtPais.getText();
        String descripcion = txtDescripcion.getText();
        if (nombre == null || nombre.trim().isEmpty() || nombre.length() > 45) {
            lblError.setText("Nombre es obligatorio y máximo 45 caracteres.");
            return false;
        }
        if (pais == null || pais.trim().isEmpty() || pais.length() > 45) {
            lblError.setText("País es obligatorio y máximo 45 caracteres.");
            return false;
        }
        if (descripcion != null && descripcion.length() > 255) {
            lblError.setText("Descripción máximo 255 caracteres.");
            return false;
        }
        lblError.setText("");
        return true;
    }

    @FXML
    private void guardarFabricante() {
        if (!validarCampos()) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Está seguro de que desea guardar este fabricante?", ButtonType.OK, ButtonType.CANCEL);
        alert.setHeaderText("Confirmar guardado");
        alert.setTitle("Confirmación");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        Fabricante f = new Fabricante();
        f.setNombre(txtNombre.getText());
        f.setPais(txtPais.getText());
        f.setDescripcion(txtDescripcion.getText());
        dao.insertar(f);
        cargarFabricantes();
        mostrarFabricante(null);
    }

    @FXML
    private void actualizarFabricante() {
        if (!validarCampos()) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Está seguro de que desea actualizar este fabricante?", ButtonType.OK, ButtonType.CANCEL);
        alert.setHeaderText("Confirmar actualización");
        alert.setTitle("Confirmación");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        Fabricante seleccionado = fabricanteTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            seleccionado.setNombre(txtNombre.getText());
            seleccionado.setPais(txtPais.getText());
            seleccionado.setDescripcion(txtDescripcion.getText());
            dao.actualizar(seleccionado);
            cargarFabricantes();
            mostrarFabricante(null);
        }
    }

    @FXML
    private void eliminarFabricante() {
        Fabricante seleccionado = fabricanteTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Está seguro de que desea eliminar este fabricante?", ButtonType.OK, ButtonType.CANCEL);
            alert.setHeaderText("Confirmar eliminación");
            alert.setTitle("Confirmación");
            if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
            dao.eliminar(seleccionado.getIdFabricante());
            cargarFabricantes();
            mostrarFabricante(null);
        }
    }

    @FXML
    private void onTableClicked() {
        Fabricante seleccionado = fabricanteTable.getSelectionModel().getSelectedItem();
        mostrarFabricante(seleccionado);
    }

    @FXML
    private void limpiarCampos() {
        txtNombre.clear();
        txtPais.clear();
        txtDescripcion.clear();
        fabricanteTable.getSelectionModel().clearSelection();
        btnGuardar.setDisable(false); // Enable Guardar after clearing
    }

    @FXML
    private void onDescripcionKeyPressed(javafx.scene.input.KeyEvent event) {
        // Prevent Ctrl+A, Alt+A, or any shortcut that opens a file dialog
        if ((event.isControlDown() || event.isAltDown()) && event.getCode().toString().equalsIgnoreCase("A")) {
            event.consume();
        }
        // Optionally, block just 'A' if needed
        // if (event.getCode().toString().equalsIgnoreCase("A")) event.consume();
    }
}
