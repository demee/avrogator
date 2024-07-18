package org.demee.avrogator;


import com.google.inject.Inject;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor
public class AvrogatorApplication extends Application {
    @Inject AvrogatorController controller;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("avrogator.fxml"));
        loader.setController(controller);
        Scene scene = new Scene(loader.load());
        stage.setTitle("Avrogator v0.0.1");
        stage.setScene(scene);
        stage.show();
    }

    public void launchApplication(String[] args) {
        launch(args);
    }
}
