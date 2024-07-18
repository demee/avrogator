package org.demee.avrogator;


import com.google.inject.Injector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor
public class AvrogatorApplication extends Application {
    private static Injector injector;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("avrogator.fxml"));
        loader.setController(injector.getInstance(AvrogatorController.class));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Avrogator v0.0.1");
        stage.setScene(scene);
        stage.show();
    }

    public void launchApplication(String[] args, Injector injector) {
        AvrogatorApplication.injector = injector;
        launch(args);
    }
}
