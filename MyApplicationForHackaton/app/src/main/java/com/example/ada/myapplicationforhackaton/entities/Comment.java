package com.example.ada.myapplicationforhackaton.entities;

/**
 * Created by Ada on 11/25/2017.
 */

public class Comment {
    private String id,sender,magazin,text;

    public Comment() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String autor) {
        this.sender = autor;
    }

    public String getMagazin() {
        return magazin;
    }

    public void setMagazin(String nume) {
        this.magazin = nume;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return sender+": "+text;
    }
}
