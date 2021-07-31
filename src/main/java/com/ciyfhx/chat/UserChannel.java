package com.ciyfhx.chat;

import io.netty.channel.Channel;

public class UserChannel extends User {

    private Channel channel;

    public UserChannel(String username, Channel channel) {
        super(username);
        this.channel = channel;
    }


    public Channel channel() {
        return channel;
    }

}
