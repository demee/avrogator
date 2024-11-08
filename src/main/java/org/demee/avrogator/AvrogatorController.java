package org.demee.avrogator;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.apache.avro.Schema;
import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;


public class AvrogatorController {
    AvroParser parser;
    AvroSqlInterface sqlInterface;  // Initialized when opening a file
    File file;
    long currentPage = 0;

    @FXML
    TextArea selectTextArea;
    @FXML
    TextArea whereTextArea;
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

    
    public AvrogatorController() {;
        parser = new AvroParser();
    }

    @FXML
    public void openFile() throws Exception {
        file = getFile();  // open javafx select file dialog
        initSqlInterface();
        if (file != null) {
            resetData();
            updateUIElements();
            renderSchema();
            renderPage();
            renderTotalCount();
        } else {
            fileNameLabel.setText("No file opened");
        }
    }

    @FXML
    public void runQuery() {
        currentPage = 0;
        renderPage();
    }

    private void initSqlInterface() throws Exception {
        if (sqlInterface != null) { // close previous connection
            sqlInterface.close();
        }
        sqlInterface = new AvroSqlInterface(file.getAbsolutePath());
        sqlInterface.init();
    }

    private void resetData() throws Exception {
        tableView.getColumns().clear();
        tableView.getItems().clear();
        schemaTableView.getItems().clear();
        schemaTableView.getColumns().clear();
    }

    private void updateUIElements() {
        fileNameLabel.setText(file.getName());
    }


    private void renderTotalCount() {
        try {
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
        String select = selectTextArea.getText();
        String where = whereTextArea.getText();
        String query = "SELECT " + select + " FROM AVRO.AVRO_TABLE " + where + " LIMIT 1000 OFFSET " + currentPage * 1000;
        System.out.println(query);
        try {
            ResultSet resultSet = sqlInterface.executeQuery(query);
            // iterate over results and render in table based on schema
            displayResults(resultSet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void displayResults(ResultSet rs) throws Exception {

        Schema schema = parser.getSchema(file);

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        tableView.getColumns().clear();
        
        for (int i = 1; i <= columnCount; i++) {
            TableColumn<Map<String, Object>, String> column = new TableColumn<>(metaData.getColumnName(i));
            final int columnIndex = i - 1;
            column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(schema.getFields().get(columnIndex).name()).toString()));
            tableView.getColumns().add(column);
        }
        

        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 0; i < columnCount; i++) {
                row.put(schema.getFields().get(i).name(), rs.getObject(i+1));
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
        if (currentPage < 0) {
            currentPage = 0;
        }
        renderPage();
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
