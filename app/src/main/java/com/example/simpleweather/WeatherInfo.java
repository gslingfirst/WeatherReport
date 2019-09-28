package com.example.simpleweather;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherInfo {
    private String Info;
    private String name;
    private String cid;
    private String date;
    private String parent;
    private String update_time;
    private WeatherDetail data;

    public void setCity(JSONObject wea_json, String s_info) throws JSONException {
        Info = s_info;
        name = wea_json.optJSONObject("cityInfo").optString("city");
        cid = wea_json.optJSONObject("cityInfo").optString("citykey");
        date = wea_json.optString("date");
        parent = wea_json.optJSONObject("cityInfo").optString("parent");
        update_time = wea_json.optJSONObject("cityInfo").optString("updateTime");
        data = new WeatherDetail(wea_json.optJSONObject("data"));
    }

    public String getInfo() {
        return Info;
    }

    public String getName(){
        return name;
    }

    public String getCid(){
        return cid;
    }

    public String getDate() {
        return date;
    }

    public String getParent() {
        return parent;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public WeatherDetail getData() {
        return data;
    }
}
