package org.demee.avrogator;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import lombok.Setter;


import java.io.File;


public class AvrogatorController  {
    @Inject private AvroParser parser;

    @FXML
    private MenuItem openFileMenuItem;

    @FXML
    public void openFile() {
        // open javafx select file dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Avro File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Avro Files", "*.avro")
        );
        File file = fileChooser.showOpenDialog(openFileMenuItem.getParentPopup().getScene().getWindow());
        if (file != null) {
            parser.parse(file);
        } else {
            System.out.println("No file selected");
        }
    }
}
