package com.example.simpleweather;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherDetail {
    private String humidity;
    private String pm25;
    private String pm10;
    private String quality;
    private String temperature;
    private String advice;
    private WeatherOfSingleday yesterday;
    private WeatherOfSingleday[] forecast = new WeatherOfSingleday[15];

    WeatherDetail(JSONObject data_json) throws JSONException {
        setWeatherDetail(data_json);
    }

    public void setWeatherDetail(JSONObject data_json) throws JSONException {
        humidity = data_json.optString("shidu");
        pm25 = data_json.optString("pm25");
        pm10 = data_json.optString("pm10");
        quality = data_json.optString("quality");
        temperature = data_json.optString("wendu");
        advice = data_json.optString("ganmao");
        yesterday = new WeatherOfSingleday(data_json.optJSONObject("yesterday"));
        JSONArray arr = data_json.optJSONArray("forecast");
        for (int i = 0; i < 15; i++) {
            forecast[i] = new WeatherOfSingleday(arr.getJSONObject(i));
        }
    }
    public String getHumidity(){
        return humidity;
    }

    public String getPm25() {
        return pm25;
    }

    public String getPm10() {
        return pm10;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getQuality() {
        return quality;
    }

    public String getAdvice() {
        return advice;
    }

    public WeatherOfSingleday getYesterday() {
        return yesterday;
    }

    public WeatherOfSingleday[] getForecast() {
        return forecast;
    }
}

