package org.demee.avrogator;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;

@Controller
public class AvrogatorController {
    AvroParser parser;

    @FXML
    private MenuItem openFileMenuItem;

    public AvrogatorController (@Autowired AvroParser parser) {
        this.parser = parser;
    }

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
