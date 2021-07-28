module com.ciyfhx.wowchat {
    requires java.base;

    requires javafx.base;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;

    requires com.google.guice;

    requires netty.all;
    requires org.slf4j;

    opens com.ciyfhx.chat;
    opens com.ciyfhx.gui.client;
    opens com.ciyfhx.gui.server;
}