package com.example.simpleweather;

import android.util.Log;

import org.json.JSONObject;

public class WeatherOfSingleday {
    private String date;
    private String high;
    private String low;
    private String ymd;
    private String week;
    private String sunset;
    private String aqi;
    private String fx;
    private String fl;
    private String type;
    private String notice;

    WeatherOfSingleday(JSONObject day_json){
        setWeather(day_json);
    }

    public void setWeather(JSONObject day_json){
        date = day_json.optString("date");
        high = day_json.optString("high");
        low = day_json.optString("low");
        ymd = day_json.optString("ymd");
        week = day_json.optString("week");
        sunset = day_json.optString("sunset");
        aqi = day_json.optString("aqi");
        fx = day_json.optString("fx");
        fl = day_json.optString("fl");
        type = day_json.optString("type");
        notice = day_json.optString("notice");
    }

    public String getDate() {
        return date;
    }

    public String getHigh() {
        return high;
    }

    public String getLow() {
        return low;
    }

    public String getWeek() {
        return week;
    }

    public String getSunset() {
        return sunset;
    }

    public String getYmd() {
        return ymd;
    }

    public String getAqi() {
        return aqi;
    }

    public String getFl() {
        return fl;
    }

    public String getFx() {
        return fx;
    }

    public String getNotice() {
        return notice;
    }

    public String getType() {
        return type;
    }

}

