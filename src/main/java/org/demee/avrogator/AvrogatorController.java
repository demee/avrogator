package org.demee.avrogator;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class AvrogatorController {
    AvroParser parser;

    public AvrogatorController() {
        parser = new AvroParser();
    }

    @FXML
    private Button openFileButton;
    @FXML
    private Label fileNameLabel;
    @FXML
    private TableView<Map<String, Object>> tableView;

    @FXML
    public void openFile() {
        // open javafx select file dialog
        File file = getFile();
        if (file != null) {
            resetData();
            updateUI(file);
            loadFile(file);
        } else {
            System.out.println("No file selected");
        }
    }

    private void resetData() {
        tableView.getColumns().clear();
        tableView.getItems().clear();
    }

    private void updateUI(File file) {
        fileNameLabel.setText(file.getName());
    }

    private void loadFile(File file) {
        renderSchema(file);
        renderRecords(file);
    }

    private void renderRecords(File file) {
        Schema schema = parser.getSchema(file);
        schema.getFields().forEach(field -> {
            TableColumn<Map<String, Object>, String> column = new TableColumn<>(field.name());
            column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(field.name()).toString()));
            tableView.getColumns().add(column);
        });

        ArrayList<GenericRecord> records = parser.parse(file);

        sortRecords(records, schema);

        records.forEach(record -> {
            Map<String, Object> row = new HashMap<>();
            schema.getFields().forEach(field -> row.put(field.name(), record.get(field.name()).toString()));
            tableView.getItems().add(row);
        });
    }

    private void sortRecords(ArrayList<GenericRecord> records, Schema schema) {
        // Get fist column name
        String firstColumnName = schema.getFields().get(0).name();
        // sort records by app_name
        records.sort(Comparator.comparing(r -> r.get(firstColumnName).toString()));
    }

    private void renderSchema(File file) {
        Schema schema = parser.getSchema(file);


    }

    private File getFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Avro File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Avro Files", "*.avro"));
        File file = fileChooser.showOpenDialog(openFileButton.getScene().getWindow());
        return file;
    }
}
