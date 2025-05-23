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
    requires com.google.gson;

    // 開放 GUI 和 Controller 給 javafx.fxml 反射使用
    opens com.app.emolist to javafx.fxml;
    opens com.app.emolist.GUI to javafx.fxml;
    opens com.app.emolist.Controller to javafx.fxml,com.google.gson;

    // 輸出模組
    exports com.app.emolist;
    exports com.app.emolist.Test;
    exports com.app.emolist.GUI;
    exports com.app.emolist.DataBase;
    exports com.app.emolist.Controller;
}
