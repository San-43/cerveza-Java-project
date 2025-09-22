package org.cerveza.cerveza.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
    @FXML private Button btnListar;
    @FXML private Label lblError;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbBuscarPor;

    private final FabricanteDaoImpl dao = new FabricanteDaoImpl();
    private ObservableList<Fabricante> fabricantes;
    private FilteredList<Fabricante> filteredFabricantes;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getIdFabricante()).asObject());
        colNombre.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getNombre()));
        colPais.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getPais()));
        colDescripcion.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDescripcion()));
        // NO cargarFabricantes() aquí para que la tabla esté vacía al inicio
        filteredFabricantes = new FilteredList<>(FXCollections.observableArrayList(), p -> true);
        fabricanteTable.setItems(filteredFabricantes);

        if (cbBuscarPor != null) {
            cbBuscarPor.setItems(FXCollections.observableArrayList("ID", "Nombre", "País", "Descripción"));
            cbBuscarPor.getSelectionModel().select("Nombre"); // Valor por defecto
            cbBuscarPor.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltroBusqueda());
        }

        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> aplicarFiltroBusqueda());
        }

        fabricanteTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            mostrarFabricante(newSel);
            btnGuardar.setDisable(newSel != null);
            btnActualizar.setDisable(newSel == null);
        });
        btnGuardar.setDisable(false);
        btnActualizar.setDisable(true);

        if (btnListar != null) {
            btnListar.setOnAction(event -> listarFabricantes());
        }
    }

    private void cargarFabricantes() {
        fabricantes = FXCollections.observableArrayList(dao.obtenerTodos());
        if (filteredFabricantes != null) {
            filteredFabricantes = new FilteredList<>(fabricantes, filteredFabricantes.getPredicate());
            fabricanteTable.setItems(filteredFabricantes);
        } else {
            fabricanteTable.setItems(fabricantes);
        }
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
        btnGuardar.setDisable(false); // Habilitar Guardar después de limpiar
        btnActualizar.setDisable(true); // Desactiva actualizar al limpiar
    }

    @FXML
    private void onDescripcionKeyPressed(javafx.scene.input.KeyEvent event) {
        // Prevenir Ctrl+A, Alt+A, o cualquier atajo que abra un diálogo de archivos
        if ((event.isControlDown() || event.isAltDown()) && event.getCode().toString().equalsIgnoreCase("A")) {
            event.consume();
        }
        // Opcionalmente, bloquear solo 'A' si es necesario
        // if (event.getCode().toString().equalsIgnoreCase("A")) event.consume();
    }

    private void aplicarFiltroBusqueda() {
        if (filteredFabricantes == null || fabricantes == null) return;
        String filtro = txtBuscar.getText();
        String atributo = cbBuscarPor.getValue();
        filteredFabricantes.setPredicate(fabricante -> {
            if (filtro == null || filtro.isEmpty() || atributo == null) return true;
            String lowerFiltro = filtro.toLowerCase();
            switch (atributo) {
                case "ID":
                    return String.valueOf(fabricante.getIdFabricante()).contains(lowerFiltro);
                case "Nombre":
                    return fabricante.getNombre() != null && fabricante.getNombre().toLowerCase().contains(lowerFiltro);
                case "País":
                    return fabricante.getPais() != null && fabricante.getPais().toLowerCase().contains(lowerFiltro);
                case "Descripción":
                    return fabricante.getDescripcion() != null && fabricante.getDescripcion().toLowerCase().contains(lowerFiltro);
                default:
                    return true;
            }
        });
    }

    @FXML
    private void listarFabricantes() {
        cargarFabricantes();
        mostrarFabricante(null);
    }
}
