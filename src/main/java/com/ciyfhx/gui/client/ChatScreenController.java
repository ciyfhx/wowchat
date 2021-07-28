package com.ciyfhx.gui.client;

import com.ciyfhx.chat.IChat;
import com.ciyfhx.chat.IMessageReceived;
import com.ciyfhx.chat.packets.NewMessagePacket;
import com.ciyfhx.chat.packets.NewServerMessagePacket;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatScreenController implements Initializable, IMessageReceived {

    @Inject
    private WowChatClientConnection connection;
    private IChat chat;

    @FXML
    private TextArea messagesTextArea;
    @FXML
    private TextField messageTextField;

    @FXML
    public void sendAction(ActionEvent event){
        this.chat.sendMessage(messageTextField.getText());
        messageTextField.clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        messageTextField.requestFocus();
        this.chat = connection.getClient().getChat();
        this.chat.setMessageReceivedListener(this);

        messagesTextArea.appendText("You have joined " + chat.getChatGroup().getChatGroupName() + "\n");
    }

    @Override
    public void onMessageReceived(NewMessagePacket message) {
        messagesTextArea.appendText("[" + message.getUsername() + "]: " + message.getMessage() + "\n");
    }

    @Override
    public void onServerMessageReceived(NewServerMessagePacket message) {
        messagesTextArea.appendText(message.getMessage() + "\n");
    }
}
