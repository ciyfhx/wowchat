package com.ciyfhx.gui.server;

import com.ciyfhx.main.WowChatServer;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

public class ServerLogController extends AppenderBase<ILoggingEvent> implements Initializable {
    @Inject
    private WowChatServerConnection connection;

    @FXML
    private TextArea serverLogTextArea;

    private Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rootLogger.addAppender(this);
        this.start();

        var server = new WowChatServer();
        this.connection.setServer(server);
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void append(ILoggingEvent event) {
        serverLogTextArea.appendText(event.getMessage() + "\n");
    }
}
