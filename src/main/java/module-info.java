module org.demee.avrogator {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.avro;
    requires spring.context;
    requires ignite.core;
    requires spring.beans;

    opens org.demee.avrogator to javafx.fxml;
    exports org.demee.avrogator;
}
