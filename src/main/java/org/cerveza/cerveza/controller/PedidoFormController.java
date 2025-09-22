package org.cerveza.cerveza.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.cerveza.cerveza.dao.impl.PedidoDaoImpl;
import org.cerveza.cerveza.model.Pedido;

import java.util.List;

public class PedidoFormController {
    @FXML private TextField txtIdPedido, txtIdExpendio;
    @FXML private Button btnGuardar, btnActualizar, btnEliminar;
    @FXML private TableView<Pedido> tblPedidos;
    @FXML private TableColumn<Pedido, Integer> colIdPedido, colIdExpendio;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbBuscarPor;
    @FXML private Button btnListar;

    private PedidoDaoImpl pedidoDao = new PedidoDaoImpl();
    private javafx.collections.ObservableList<Pedido> pedidosData;
    private javafx.collections.transformation.FilteredList<Pedido> filteredPedidos;

    @FXML
    public void initialize() {
        colIdPedido.setCellValueFactory(new PropertyValueFactory<>("idpedido"));
        colIdExpendio.setCellValueFactory(new PropertyValueFactory<>("idexpendio"));
        // Tabla vacía al inicio
        pedidosData = FXCollections.observableArrayList();
        filteredPedidos = new javafx.collections.transformation.FilteredList<>(pedidosData, p -> true);
        tblPedidos.setItems(filteredPedidos);
        // Configurar ComboBox de búsqueda
        if (cbBuscarPor != null) {
            cbBuscarPor.setItems(FXCollections.observableArrayList("ID Pedido", "ID Expendio"));
            cbBuscarPor.getSelectionModel().select("ID Pedido");
            cbBuscarPor.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltroBusqueda());
        }
        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> aplicarFiltroBusqueda());
        }
        if (btnListar != null) {
            btnListar.setOnAction(e -> onListar());
        }
        btnActualizar.setDisable(true);
        btnGuardar.setDisable(false);
        tblPedidos.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                fillForm(newSel);
                btnGuardar.setDisable(true);
                btnActualizar.setDisable(false);
            } else {
                btnGuardar.setDisable(false);
                btnActualizar.setDisable(true);
            }
        });
    }

    private void refreshTable() {
        // Ya no se usa para listar inicial, pero se puede mantener para otros usos internos
        List<Pedido> pedidos = pedidoDao.findAll();
        pedidosData.setAll(pedidos);
        aplicarFiltroBusqueda();
    }

    private void fillForm(Pedido pedido) {
        txtIdPedido.setText(String.valueOf(pedido.getIdpedido()));
        txtIdExpendio.setText(String.valueOf(pedido.getIdexpendio()));
    }

    @FXML
    private void onNuevo() {
        txtIdPedido.clear();
        txtIdExpendio.clear();
        tblPedidos.getSelectionModel().clearSelection();
    }

    @FXML
    private void onGuardar() {
        try {
            int idPedido = txtIdPedido.getText().isEmpty() ? 0 : Integer.parseInt(txtIdPedido.getText());
            int idExpendio = Integer.parseInt(txtIdExpendio.getText());
            Pedido pedido = new Pedido(idPedido, idExpendio);
            boolean isNew = idPedido == 0 || !pedidoDao.findById(idPedido).isPresent();
            String action = isNew ? "agregar" : "actualizar";
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Seguro que desea " + action + " este pedido?", ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText("Confirmar " + (isNew ? "agregado" : "actualización"));
            confirm.setTitle("Confirmación");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    if (isNew) {
                        pedidoDao.insert(pedido);
                    } else {
                        pedidoDao.update(pedido);
                    }
                    refreshTable();
                    onLimpiar();
                }
            });
        } catch (Exception e) {
            showError("Error al guardar: " + e.getMessage());
        }
    }

    @FXML
    private void onActualizar() {
        Pedido selected = tblPedidos.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                int idPedido = selected.getIdpedido();
                int idExpendio = Integer.parseInt(txtIdExpendio.getText());
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Seguro que desea actualizar este pedido?", ButtonType.YES, ButtonType.NO);
                confirm.setHeaderText("Confirmar actualización");
                confirm.setTitle("Confirmación");
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        Pedido pedido = new Pedido(idPedido, idExpendio);
                        pedidoDao.update(pedido);
                        refreshTable();
                        onLimpiar();
                    }
                });
            } catch (Exception e) {
                showError("Error al actualizar: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onListar() {
        List<Pedido> pedidos = pedidoDao.findAll();
        pedidosData.setAll(pedidos);
        aplicarFiltroBusqueda();
    }

    private void aplicarFiltroBusqueda() {
        if (filteredPedidos == null || pedidosData == null) return;
        String filtro = txtBuscar != null ? txtBuscar.getText() : null;
        String atributo = cbBuscarPor != null ? cbBuscarPor.getValue() : null;
        filteredPedidos.setPredicate(pedido -> {
            if (filtro == null || filtro.isEmpty() || atributo == null) return true;
            String lowerFiltro = filtro.toLowerCase();
            switch (atributo) {
                case "ID Pedido":
                    return String.valueOf(pedido.getIdpedido()).contains(lowerFiltro);
                case "ID Expendio":
                    return String.valueOf(pedido.getIdexpendio()).contains(lowerFiltro);
                default:
                    return true;
            }
        });
    }

    @FXML
    private void onLimpiar() {
        txtIdPedido.clear();
        txtIdExpendio.clear();
        tblPedidos.getSelectionModel().clearSelection();
        btnGuardar.setDisable(false);
        btnActualizar.setDisable(true);
    }

    @FXML
    private void onEliminar() {
        Pedido selected = tblPedidos.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Seguro que desea eliminar este pedido?", ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText("Confirmar eliminación");
            confirm.setTitle("Confirmación");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    pedidoDao.delete(selected.getIdpedido());
                    refreshTable();
                    onLimpiar();
                }
            });
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
