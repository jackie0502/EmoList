module com.app.emolist {
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

    opens com.app.emolist to javafx.fxml;
    exports com.app.emolist;
    exports com.app.emolist.Test;
    exports com.app.emolist.GUI;
    exports com.app.emolist.DataBase;
    exports com.app.emolist.Controller;
    opens com.app.emolist.Controller to javafx.fxml;
}