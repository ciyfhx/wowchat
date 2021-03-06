package com.ciyfhx.gui;

import com.ciyfhx.chat.Closeable;
import com.ciyfhx.gui.client.MainClientGUI;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FXMLUtils {

    public static void newStage(String resourceName) throws IOException {
        var loader = new FXMLLoader(FXMLUtils.class.getClassLoader().getResource(resourceName));
        loader.setControllerFactory(MainClientGUI.INJECTOR::getInstance);
        var root = (Parent) loader.load();
        var controller = loader.getController();
        var stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setOnHidden(e -> {
            if(controller instanceof Closeable closeable)
                closeable.close();
        });
        stage.show();
    }

}
