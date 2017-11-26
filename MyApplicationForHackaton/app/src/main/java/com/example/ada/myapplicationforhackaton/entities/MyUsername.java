package com.example.ada.myapplicationforhackaton.entities;

/**
 * Created by Ada on 11/25/2017.
 */

public class MyUsername {
    private String name;
    private boolean isMagazin;

    public boolean isMagazin() {
        return isMagazin;
    }

    public void setMagazin(boolean magazin) {
        isMagazin = magazin;
    }

    private static MyUsername INSTANCE;
    private MyUsername(){

    }

    private static final class SingeltonHolder{

        private static final MyUsername INSTANCE = new MyUsername();


    }
    public static MyUsername getInstance(){
        return SingeltonHolder.INSTANCE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}