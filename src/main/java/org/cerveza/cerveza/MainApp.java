package org.cerveza.cerveza;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;


public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        URL url = MainApp.class.getResource("/fxml/main.fxml");
        if (url == null) {
            url = Thread.currentThread().getContextClassLoader().getResource("fxml/main.fxml");
        }
        System.out.println("FXML URL = " + url); // Debe imprimir algo NO nulo
        Parent root = FXMLLoader.load(Objects.requireNonNull(url, "No se encontró fxml/main.fxml"));

        Scene scene = new Scene(root, 1100, 700);
        URL cssUrl = MainApp.class.getResource("/css/app.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.out.println("WARN: /css/app.css no encontrado; se continúa sin estilos.");
        }
        stage.setTitle("MyDB · Gestión de Cervezas");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) { launch(args); }
}
