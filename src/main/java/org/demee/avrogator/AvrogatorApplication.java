package org.demee.avrogator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AvrogatorApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AvrogatorApplication.class.getResource("avrogator.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Avrogator v0.0.1");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Load properties from application.properties
        Properties properties = new Properties();
        try (InputStream input = AvrogatorApplication.class.getResourceAsStream("/application.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
                return;
            }
            // Load properties file
            properties.load(input);

            // Set system properties
            properties.forEach((key, value) -> System.setProperty((String) key, (String) value));
        } catch (IOException ex) {
            ex.printStackTrace();
        }


        launch();
    }
}
