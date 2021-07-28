package com.ciyfhx.gui.client;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainClientGUI extends Application {

    public final static Injector INJECTOR = Guice.createInjector(new WowChatModule());

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        var loader = new FXMLLoader(this.getClass().getClassLoader().getResource("login_screen.fxml"));
        loader.setControllerFactory(INJECTOR::getInstance);

        var root = (Parent) loader.load();
        primaryStage.setTitle("WowChat Client");
        primaryStage.setScene(new Scene(root, 300, 400));
        primaryStage.show();
    }
}
