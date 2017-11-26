package com.example.ada.myapplicationforhackaton.entities;

/**
 * Created by Ada on 11/26/2017.
 */

public class Message {
    private String id,user,magazin,text;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String utilizator) {
        this.user = utilizator;
    }

    public String getMagazin() {
        return magazin;
    }

    public void setMagazin(String magazin) {
        this.magazin = magazin;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Message() {
    }
}
