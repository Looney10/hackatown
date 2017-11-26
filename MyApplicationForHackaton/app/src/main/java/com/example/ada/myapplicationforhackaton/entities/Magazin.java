package com.example.ada.myapplicationforhackaton.entities;

import java.io.Serializable;

/**
 * Created by Ada on 11/25/2017.
 */

public class Magazin implements Serializable {
    private String id,nume,descriere,logourl;

    public Magazin() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    public String getLogourl() {
        return logourl;
    }

    public void setLogourl(String logourl) {
        this.logourl = logourl;
    }
}
