module org.demee.avrogator {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires transitive javafx.graphics;
    requires transitive org.apache.avro;
    requires lombok;
    requires java.sql;
    requires calcite.core;
    requires calcite.file;
    requires calcite.linq4j;

    opens org.demee.avrogator to javafx.fxml;
    exports org.demee.avrogator;
}
