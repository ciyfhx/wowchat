module com.ciyfhx.wowchat {
    requires java.base;

    requires javafx.base;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;

    requires com.google.guice;

    requires io.netty.all;
    requires org.slf4j;
    requires logback.core;
    requires logback.classic;

    opens com.ciyfhx.chat;
    opens com.ciyfhx.gui.client;
    opens com.ciyfhx.gui.server;
    opens com.ciyfhx.chat.server;
    opens com.ciyfhx.chat.client;
    opens com.ciyfhx.chat.network;
}