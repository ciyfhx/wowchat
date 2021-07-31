package com.ciyfhx.gui.client;

import com.ciyfhx.chat.ChatGroup;
import com.ciyfhx.chat.Closeable;
import com.ciyfhx.chat.client.ClientChatGroup;
import com.ciyfhx.chat.client.IChatLobby;
import com.ciyfhx.chat.client.IJoinedChatGroup;
import com.ciyfhx.chat.client.IListChatGroups;
import com.ciyfhx.gui.FXMLUtils;
import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class ListChatGroupsController implements Initializable, Closeable, IListChatGroups, IJoinedChatGroup {

    @Inject
    private WowChatClientConnection connection;
    private IChatLobby chat;

    @FXML
    private TreeTableView<ClientChatGroup> tableView;
    @FXML
    private TreeTableColumn<ChatGroup, String> chatGroupColumn;
    @FXML
    private TreeTableColumn<ChatGroup, String> usersColumn;

    @FXML
    private void createChatGroupAction(ActionEvent event){
        var dialog = new TextInputDialog();
        dialog.setTitle("New Chat Group");
        dialog.setHeaderText("Chat Group Name");
        dialog.setContentText("Please enter the chat group name:");

        var result = dialog.showAndWait();
        result.ifPresent(s -> this.chat.createChatGroup(s));
        this.chat.listChatGroups();
    }

    @FXML
    private void refreshAction(ActionEvent event){
        this.chat.listChatGroups();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.chat = this.connection.getClient().getChatLobby();
        this.chat.addListChatGroupsListener(this);
        this.chat.addJoinedChatGroupListener(this);

        chatGroupColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("chatGroupName"));
        usersColumn.setCellValueFactory(data -> {
            var chatGroup = data.getValue().getValue();
            if(chatGroup == null) return new SimpleStringProperty("");
            return new SimpleStringProperty(chatGroup.getNumberOfUsersInChatGroup()+ "/" + chatGroup.getSizeOfChatGroup());
        });
        this.tableView.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2){
                ClientChatGroup selectedChatGroup = tableView.getSelectionModel().getSelectedItem().getValue();
                this.chat.joinChatGroup(selectedChatGroup);
            }
        });
        this.chat.listChatGroups();
    }

    @Override
    public void close() {
        this.chat.removeListChatGroupsListener(this);
        this.chat.removeJoinedChatGroupListener(this);
    }

    @Override
    public void onListChatGroups(Collection<ClientChatGroup> chatGroups) {
        var root = new TreeItem<ClientChatGroup>();
        for(ClientChatGroup chatGroup : chatGroups)root.getChildren().add(new TreeItem<>(chatGroup));
        Platform.runLater(() -> {
            tableView.setRoot(root);
            tableView.setShowRoot(false);
            tableView.refresh();
        });
    }
    private void showChatScreen(){
        Platform.runLater(() -> {
            try {
                FXMLUtils.newStage("chat_screen.fxml");
                Stage stage = (Stage) tableView.getScene().getWindow();
                stage.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

    @Override
    public void onJoinedChatGroup(ClientChatGroup chatGroup) {
        this.connection.setCurrentChatGroup(chatGroup);
        showChatScreen();
    }
}
