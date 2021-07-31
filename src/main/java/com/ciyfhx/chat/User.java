package com.ciyfhx.chat;

import java.util.Objects;

public class User{

    private String username;

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return username;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof User user) return hashCode() == user.hashCode();
        return false;
    }
}
