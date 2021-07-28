package com.ciyfhx.gui.server;

import com.ciyfhx.main.WowChatServer;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class ServerLogController implements Initializable {
    @Inject
    private WowChatServerConnection connection;

    @FXML
    private TextArea serverLogTextArea;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        var server = new WowChatServer();
        this.connection.setServer(server);
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
