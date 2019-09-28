package com.example.simpleweather;

import android.util.Log;

public class Province {
    private String name;
    private City[] cities;
    int num_of_city;

    Province(){
        num_of_city = 0;
        name = "NULL";
        cities = new City[36];
        for(int i = 0; i < 36; i++)
        {
            cities[i] = new City();
        }
    }

    public City[] getCityIDS() {
        return cities;
    }

    public String getName() {
        return name;
    }

    public int getNum_of_city() {
        return num_of_city;
    }

    public void setCityIDS(City[] cityIDS) {
        for(int i = 0; i < num_of_city; i++)
        {
            this.cities[i].setName(cityIDS[i].getName());
            this.cities[i].setId(cityIDS[i].getId());
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNum_of_city(int num_of_city) {
        this.num_of_city = num_of_city;
    }
}

