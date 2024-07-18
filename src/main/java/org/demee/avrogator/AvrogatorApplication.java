package org.demee.avrogator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor
public class AvrogatorApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("avrogator.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Avrogator v0.0.1");
        stage.setScene(scene);
        stage.show();
    }

    public void launchApplication(String[] args) {
        launch(args);
    }
}
