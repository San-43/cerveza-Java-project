module org.cerveza.cerveza {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires annotations;

    opens org.cerveza.cerveza to javafx.fxml;
    opens org.cerveza.cerveza.controller to javafx.fxml; // para fx:controller
    opens org.cerveza.cerveza.model to javafx.base;
    exports org.cerveza.cerveza;
}