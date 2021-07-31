package com.ciyfhx.chat.client;

import com.ciyfhx.chat.User;

import java.util.Set;

public interface IListUsersInChatGroup {

    void onListUsers(Set<User> users);
    void onUserJoined(User userJoined);
    void onUserLeave(User userLeave);

}
