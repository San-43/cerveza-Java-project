package org.cerveza.cerveza.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.Objects;

public class MainController {
    @FXML private StackPane contentPane; // en main.fxml debe existir fx:id="contentPane"

    @FXML public void onSalir() { System.exit(0); }

    @FXML public void onAcercaDe() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText("MyDB · Gestión de Cervezas");
        a.setContentText("JavaFX + FXML + MySQL. © 2025");
        a.showAndWait();
    }

    @FXML public void openCervezaForm() { setCenter("/fxml/cerveza-form.fxml"); }
    @FXML public void openMarcaForm()   { setCenter("/fxml/marca-form.fxml"); }
    @FXML public void openEnvaseForm()  { setCenter("/fxml/envase-form.fxml"); }
    @FXML public void openProduccionForm()  { setCenter("/fxml/produccion-form.fxml"); }
    @FXML public void openExpendioForm()  { setCenter("/fxml/expendio-form.fxml"); }
    @FXML public void openExistenciaForm()  { setCenter("/fxml/existencia-form.fxml"); }
    @FXML public void openFabricanteForm()  { setCenter("/fxml/fabricante-form.fxml"); }
    @FXML public void openIngredienteForm()  { setCenter("/fxml/ingrediente-form.fxml"); }
    @FXML public void openOrdenForm()  { setCenter("/fxml/orden-form.fxml"); }
    @FXML public void openPedidoForm()  { setCenter("/fxml/pedidos-form.fxml"); }
    @FXML public void openPresentacionForm()  { setCenter("/fxml/presentacion-form.fxml"); }
    @FXML public void openRecetaForm()  { setCenter("/fxml/receta-form.fxml"); }
    @FXML public void openVentaForm()  { setCenter("/fxml/venta-form.fxml"); }

    private void setCenter(String absoluteFxmlPath) {
        try {
            URL url = Objects.requireNonNull(
                    MainController.class.getResource(absoluteFxmlPath),
                    "No se encontró " + absoluteFxmlPath + " en el classpath"
            );
            System.out.println("Cargando FXML = " + url);
            Node view = new FXMLLoader(url).load(); // location configurada
            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
