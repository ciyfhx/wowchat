package com.ciyfhx.gui.client;

import com.ciyfhx.chat.*;
import com.ciyfhx.chat.User;
import com.ciyfhx.chat.client.*;
import com.ciyfhx.chat.packets.NewMessagePacket;
import com.ciyfhx.chat.packets.NewServerMessagePacket;
import com.ciyfhx.gui.FXMLUtils;
import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public class ChatScreenController implements Initializable, Closeable, IMessageReceived, IListUsersInChatGroup {

    @Inject
    private WowChatClientConnection connection;
    private IChatLobby lobby;
    private ClientChatGroup chat;

    @FXML
    private ListView<User> usersListView;
    private ObservableList<User> users;
    @FXML
    private TextArea messagesTextArea;
    @FXML
    private TextField messageTextField;

    @FXML
    public void sendAction(ActionEvent event){
        this.chat.sendMessage(messageTextField.getText());
        messageTextField.clear();
    }

    @FXML
    public void leaveChatGroup(ActionEvent event){
        this.lobby.leaveChatGroup(chat);
        showChatGroupsScreen();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        messageTextField.requestFocus();
        this.lobby = connection.getClient().getChatLobby();
        this.chat = connection.getCurrentChatGroup();
        this.chat.addListUsersInChatGroupListener(this);
        this.chat.addMessageReceivedListener(this);

        this.users = FXCollections.observableArrayList(
                this.chat.getUsers().stream().toList()
        );
        this.usersListView.setItems(this.users);

        messagesTextArea.appendText("You have joined " + chat.getChatGroupName() + "\n");
    }

    @Override
    public void close() {
        this.chat.removeListUsersInChatGroupListener(this);
        this.chat.removeMessageReceivedListener(this);
    }

    @Override
    public void onMessageReceived(NewMessagePacket message) {
        messagesTextArea.appendText("[" + message.getUsername() + "]: " + message.getMessage() + "\n");
    }

    @Override
    public void onServerMessageReceived(NewServerMessagePacket message) {
        messagesTextArea.appendText(message.getMessage() + "\n");
    }

    @Override
    public void onListUsers(Set<User> users) {
        this.users = FXCollections.observableArrayList(
                users.stream().toList()
        );
        this.usersListView.setItems(this.users);
    }

    @Override
    public void onUserJoined(User userJoined) {
        Platform.runLater(() -> this.users.add(userJoined));
    }

    @Override
    public void onUserLeave(User userLeave) {
        Platform.runLater(() -> this.users.remove(userLeave));
    }

    private void showChatGroupsScreen(){
        Platform.runLater(() -> {
            try {
                FXMLUtils.newStage("list_chat_groups_screen.fxml");
                Stage stage = (Stage) usersListView.getScene().getWindow();
                stage.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

}
