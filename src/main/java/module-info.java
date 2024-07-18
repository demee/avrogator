module org.demee.avrogator {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.avro;
    requires com.google.guice;
    requires lombok;

    opens org.demee.avrogator to javafx.fxml, com.google.guice;
    exports org.demee.avrogator;
    exports org.demee.avrogator.di;
    opens org.demee.avrogator.di to javafx.fxml;
}
