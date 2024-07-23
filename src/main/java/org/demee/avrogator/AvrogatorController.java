package org.demee.avrogator;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class AvrogatorController {
    AvroParser parser;
    AvroSqlInterface sqlInterface;
    File file;
    long currentPage = 0;

    public AvrogatorController() {;
        parser = new AvroParser();
    }

    @FXML
    private Button openFileButton;
    @FXML
    private Label fileNameLabel;
    @FXML
    private Label totalCountLabel;
    @FXML
    private TableView<Map<String, Object>> tableView;
    @FXML 
    private TableView<Map<String, Object>> schemaTableView;

    @FXML
    private Label pageLabel;
    @FXML
    private Button nextPageButton;
    @FXML
    private Button previousPageButton;

    @FXML
    public void openFile() {
        // open javafx select file dialog
        file = getFile();
        sqlInterface = new AvroSqlInterface(file.getAbsolutePath());
        if (file != null) {
            resetData();
            updateUI();
            loadFile();
        } else {
            System.out.println("No file selected");
        }
    }

    private void resetData() {
        tableView.getColumns().clear();
        tableView.getItems().clear();
        schemaTableView.getItems().clear();
        schemaTableView.getColumns().clear();
    }

    private void updateUI() {
        fileNameLabel.setText(file.getName());
    }

    private void loadFile() {
        renderSchema();
        renderPage();
        renderTotalCount();
    }

    private void renderTotalCount() {

        try {
            sqlInterface.init();
            ResultSet resultSet = sqlInterface.executeQuery("SELECT COUNT(*) FROM AVRO.AVRO_TABLE");
            if (resultSet.next()) {
                totalCountLabel.setText("Total Count: " + resultSet.getInt(1));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void renderPage() {
        pageLabel.setText("Page: " + currentPage);
        tableView.getItems().clear();
        try {
            ResultSet resultSet = sqlInterface.executeQuery("SELECT * FROM AVRO.AVRO_TABLE LIMIT 1000 OFFSET " + currentPage * 1000);
            // iterate over results and render in table based on schema
            displayResults(resultSet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void displayResults(ResultSet rs) throws Exception {
        tableView.getColumns().clear();
        tableView.getItems().clear();
        Schema schema = parser.getSchema(file);

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();


        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                row.put(schema.getFields().get(i).name(), rs.getObject(i));
            }
            tableView.getItems().add(row);
        }
    }

    @FXML
    private void nextPage() {
        currentPage++;
        renderPage();
    }
    @FXML
    private void previousPage() {
        currentPage--;
        renderPage();
    }

    private void renderRecords(File file) {
        Schema schema = parser.getSchema(file);
        renderColumns(schema);

        ArrayList<GenericRecord> records = parser.parse(file);

        sortRecords(records, schema);

        records.forEach(record -> {
            Map<String, Object> row = new HashMap<>();
            schema.getFields().forEach(field -> row.put(field.name(), record.get(field.name()).toString()));
            tableView.getItems().add(row);
        });
    }

    private void renderColumns(Schema schema) {
        schema.getFields().forEach(field -> {
            TableColumn<Map<String, Object>, String> column = new TableColumn<>(field.name());
            column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(field.name()).toString()));
            tableView.getColumns().add(column);
        });
    }

    private void sortRecords(ArrayList<GenericRecord> records, Schema schema) {
        // Get fist column name
        String firstColumnName = schema.getFields().get(0).name();
        // sort records by app_name
        records.sort(Comparator.comparing(r -> r.get(firstColumnName).toString()));
    }

    private void renderSchema() {
        Schema schema = parser.getSchema(file);
        createSchemaColumns();
      
        schema.getFields().forEach(field -> {
            Map<String, Object> row = new HashMap<>();
            row.put("name", field.name());
            if (field.schema().getType().equals(Schema.Type.UNION)) {
                row.put("type", field.schema().getTypes().toString());
            } else {
                row.put("type", field.schema().getType().toString().toLowerCase());
            }
            schemaTableView.getItems().add(row);
        });

    }

    private void createSchemaColumns() {
        TableColumn<Map<String, Object>, String> nameColumn = new TableColumn<>("name");
        nameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get("name").toString()));
        schemaTableView.getColumns().add(nameColumn);
        TableColumn<Map<String, Object>, String> typeColumn = new TableColumn<>("type");
        typeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get("type").toString()));
        schemaTableView.getColumns().add(typeColumn);

    }

    private File getFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Avro File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Avro Files", "*.avro"));
        File file = fileChooser.showOpenDialog(openFileButton.getScene().getWindow());
        return file;
    }
}
