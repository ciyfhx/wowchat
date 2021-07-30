package com.ciyfhx.gui.server;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainServerGUI extends Application {

    public final static Injector INJECTOR = Guice.createInjector(new WowChatServerModule());

    private Logger logger = LoggerFactory.getLogger(MainServerGUI.class);

    @Inject
    private WowChatServerConnection connection;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        var loader = new FXMLLoader(this.getClass().getClassLoader().getResource("server_log.fxml"));
        INJECTOR.injectMembers(this);
        loader.setControllerFactory(INJECTOR::getInstance);

        var root = (Parent) loader.load();
        primaryStage.setTitle("WowChat Server");
        primaryStage.setScene(new Scene(root, 400, 600));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        try{
            connection.getServer().stop();
        }catch (IllegalStateException e){
            logger.error(e.getMessage());
        }
    }
}
