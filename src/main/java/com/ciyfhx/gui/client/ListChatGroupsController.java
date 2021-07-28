package com.ciyfhx.gui.client;

import com.ciyfhx.chat.BasicChatGroup;
import com.ciyfhx.chat.IChat;
import com.ciyfhx.chat.IListChatGroups;
import com.ciyfhx.gui.FXMLUtils;
import com.google.inject.Inject;
import javafx.application.Platform;
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

public class ListChatGroupsController implements Initializable, IListChatGroups {

    @Inject
    private WowChatClientConnection connection;
    private IChat chat;

    @FXML
    private TreeTableView<BasicChatGroup> tableView;
    @FXML
    private TreeTableColumn<BasicChatGroup, String> chatGroupColumn;

    @FXML
    private void createChatGroupAction(ActionEvent event){
        var dialog = new TextInputDialog();
        dialog.setTitle("New Chat Group");
        dialog.setHeaderText("Chat Group Name");
        dialog.setContentText("Please enter the chat group name:");

        var result = dialog.showAndWait();
        result.ifPresent(s -> this.chat.createGroup(s).addListener(future -> showChatScreen()));
        this.chat.listChatGroups();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.chat = this.connection.getClient().getChat();
        this.chat.setListChatGroupsListener(this);

        chatGroupColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("chatGroupName"));
        this.tableView.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2){
                BasicChatGroup selectedChatGroup = tableView.getSelectionModel().getSelectedItem().getValue();
                this.chat.joinChatGroup(selectedChatGroup).addListener(future -> this.showChatScreen());
            }
        });
        this.chat.listChatGroups();
    }

    @Override
    public void onListChatGroups(Collection<? extends BasicChatGroup> chatGroups) {
        var root = new TreeItem<BasicChatGroup>();
        for(BasicChatGroup chatGroup : chatGroups)root.getChildren().add(new TreeItem<>(chatGroup));
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

}
