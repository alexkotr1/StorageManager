module com.alexk.storagemanager {
    requires javafx.controls;
    requires javafx.fxml;
        requires javafx.web;
            
        requires org.controlsfx.controls;
            requires com.dlsc.formsfx;
                requires org.kordamp.ikonli.javafx;
    requires java.sql;
    requires commons.dbcp2;
    requires org.jetbrains.annotations;

    exports com.alexk.storagemanagererp;
    opens com.alexk.storagemanagererp.storage to javafx.base,javafx.fxml;
    opens com.alexk.storagemanagererp to javafx.base, javafx.fxml;
    exports com.alexk.storagemanagererp.cellFactories;
    opens com.alexk.storagemanagererp.cellFactories to javafx.base, javafx.fxml;
}

