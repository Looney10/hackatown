package com.example.ada.myapplicationforhackaton.entities;

/**
 * Created by Ada on 11/25/2017.
 */

public class User {
    private String id, username, password;
    private boolean isMagazin;

    public boolean isMagazin() {
        return isMagazin;
    }

    public void setMagazin(boolean magazin) {
        isMagazin = magazin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User() {
    }

}
