package com.example.simpleweather;

import android.util.Log;

public class City {
    private String name;
    private String id;

    City(){
        name = "NULL";
        id = "00000000";
    }

    public String getId() {
        return id;
    }

    public String getName(){
        return name;
    }
    public void setInfo(String Info){
        String[] split_info = Info.split(":");
        name = split_info[0];
        id = split_info[1];
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}

