package com.ciyfhx.gui.client;

import com.ciyfhx.chat.IChat;
import com.ciyfhx.gui.FXMLUtils;
import com.ciyfhx.main.WowChatClient;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginScreenController implements Initializable {

    @Inject
    private WowChatClientConnection connection;
    private IChat chat;

    @FXML
    private TextField usernameTextField;

    @FXML
    public void connectToServer(ActionEvent event) throws IOException {

        chat.sendUserInfo(usernameTextField.getText());

        FXMLUtils.newStage("list_chat_groups_screen.fxml");
        Stage stage = (Stage) usernameTextField.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            var client = new WowChatClient();
            client.setClientConnected(chat -> this.chat = chat);
            this.connection.setClient(client);
            this.connection.getClient().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
