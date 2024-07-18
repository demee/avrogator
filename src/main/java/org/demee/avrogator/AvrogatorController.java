package org.demee.avrogator;

import com.google.inject.Inject;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class AvrogatorController  {
    @Inject AvroParser parser;

    @FXML
    private MenuItem openFileMenuItem;

    @FXML
    private TableView<Map<String, Object>> tableView;

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
            Schema schema = parser.getSchema(file);
            schema.getFields().forEach(field -> {
                TableColumn<Map<String, Object>, String> column = new TableColumn<>(field.name());
                column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(field.name()).toString()));
                tableView.getColumns().add(column);;
            });

            ArrayList<GenericRecord> records = parser.parse(file);
            // Get fist column name
            String firstColumnName = schema.getFields().get(0).name();

            // sort records by app_name
            records.sort(Comparator.comparing(r -> r.get(firstColumnName).toString()));

            records.forEach(record -> {
                Map<String, Object> row = new HashMap<>();
                schema.getFields().forEach(field -> row.put(field.name(), record.get(field.name()).toString()));
                tableView.getItems().add(row);
            });
        } else {
            System.out.println("No file selected");
        }
    }
}
