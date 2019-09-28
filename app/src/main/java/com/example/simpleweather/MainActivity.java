package com.example.simpleweather;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public static final int UPDATE_INFO = 1;
    private GetInfo subp = new GetInfo();
    final WeatherInfo weatherOfCity = new WeatherInfo();
    private boolean status = true;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){  //用于与主程序交互
            switch (msg.what) {
                case UPDATE_INFO:
                    performData(weatherOfCity);
                    break;
                default:
                    break;
            }
        }
    };

    class GetInfo implements Runnable{   //自定义线程定时获取网络数据
        private String cid;
        GetInfo(){
            cid = "101010100";
        }

        @Override
        public void run() {
            getwebinfo(cid, weatherOfCity);//获取网络数据，实时更新数据
            //Log.d("abd", "in thread: " + cid);
            Message message = new Message();
            message.what = UPDATE_INFO;
            handler.sendMessage(message);
        }
        public String getCid() {
            return cid;
        }

        public void setCid(String s){
            cid = s;
        }
    }

    //更新intent，接收传来的城市选择结果
    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("abd","onNewIntent");
        super.onNewIntent(intent);
        setIntent(intent);
        Intent intent1 = getIntent();
        String cityid = intent1.getStringExtra("cityID");
        Log.d("abd",cityid);
        subp.setCid(cityid);
        GetInfo chooseCityBack = new GetInfo();
        chooseCityBack.setCid(cityid);
        new Thread(chooseCityBack).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String result = readSave();   //读取保存的信息
        updateInfo(result, weatherOfCity); //初始加载本地信息，更新界面
        performData(weatherOfCity);        //将获取的信息展示出来

        ScheduledExecutorService service = Executors.newScheduledThreadPool(2); //定时器
        long initialDelay = 0;
        long period = 30;
        service.scheduleWithFixedDelay(subp, initialDelay, period, TimeUnit.MINUTES); //增加定时任务，每30分钟刷新一次

        ActionBar actionbar = getSupportActionBar(); //隐藏默认标题栏
        if(actionbar != null)
        {
            actionbar.hide();
        }

        ImageView city = (ImageView) findViewById(R.id.cities);
        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this,"i am a test",Toast.LENGTH_SHORT);
                //LinearLayout fl = (LinearLayout) findViewById(R.id.main_window);
                //fl.setBackgroundResource(R.drawable.rain);
                Intent intent = new Intent("com.example.simpleweather.ProvinceMenu");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        //报存信息
        SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
        editor.putString("city", weatherOfCity.getInfo());
        editor.apply();
        super.onDestroy();
    }
    private void performData(WeatherInfo weatherOfCity){
        String  name = weatherOfCity.getName();
        TextView city_name = (TextView) findViewById(R.id.city_name);
        city_name.setText(name);

        String get_date = weatherOfCity.getDate();
        String date = get_date.substring(0,4) + "年" + get_date.substring(4,6) + "月" + get_date.substring(6,8) + "日 " + weatherOfCity.getData().getForecast()[0].getWeek();
        TextView updateDate = (TextView) findViewById(R.id.date);
        updateDate.setText(date);

        String temperature = weatherOfCity.getData().getTemperature();
        TextView tem_num = (TextView) findViewById(R.id.tem_num);
        tem_num.setText(temperature);

        String type = weatherOfCity.getData().getForecast()[0].getType();
        TextView wea_type = (TextView) findViewById(R.id.weather_condition);
        wea_type.setText(type);

        String range = weatherOfCity.getData().getForecast()[0].getLow().substring(3) + "--" + weatherOfCity.getData().getForecast()[0].getHigh().substring(3);
        TextView tem_range = (TextView) findViewById(R.id.tem_range);
        tem_range.setText(range);

        String quality = "空气质量: " + weatherOfCity.getData().getQuality();
        TextView air_quality = (TextView) findViewById(R.id.air_quality);
        air_quality.setText(quality);

        String wind = weatherOfCity.getData().getForecast()[0].getFx() + ": " + weatherOfCity.getData().getForecast()[0].getFl();
        TextView wind_condition = (TextView) findViewById(R.id.wind);
        wind_condition.setText(wind);
        if(!status)
        {
            Toast.makeText(MainActivity.this,"抱歉，当前不提供您选择地区的天气！！！",Toast.LENGTH_SHORT).show();
            status = true;
        }
    }
    //读取json文件
    private String readJsonFromFile(){
        InputStreamReader inputStreamReader;
        String result = new String();
        try{
            inputStreamReader = new InputStreamReader(getAssets().open("storage.json"),"UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while((line = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(line);
            }
            inputStreamReader.close();
            bufferedReader.close();
            result = stringBuilder.toString();
            //Log.d("abd",result);
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return result;
    }
    //获取网络信息
    private void getwebinfo(String cid, WeatherInfo weatherOfCity) {
        try {
            String f_url = "http://t.weather.sojson.com/api/weather/city/" + cid;
            //String f_url = "https://www.tianqiapi.com/api/?version=v1&cityid=101110101&appid=56382583&appsecret=usBAj6yU";
            URL url = new URL(f_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(reader);

            StringBuffer buffer = new StringBuffer();
            String temp = null;

            while ((temp = bufferedReader.readLine()) != null) {
                buffer.append(temp);
            }
            bufferedReader.close();
            reader.close();
            inputStream.close();
            try {
                JSONObject web_info = new JSONObject(buffer.toString());
                if(web_info.optString("status").equals("200"))
                {
                    updateInfo(buffer.toString(), weatherOfCity);
                }
                else
                {
                    status = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Log.d("abd",buffer.toString());

        }catch(MalformedURLException e)
        {
            Log.d("abd","test,test,test1");
            e.printStackTrace();
        }catch(IOException e){
            Log.d("abd","test,test,test2");
            e.printStackTrace();
        }
    }
    //更新city对象属性
    private void updateInfo(String newInfo, WeatherInfo weatherOfCity){
        //JSONObject update_info = null;
        try {
            JSONObject update_info = new JSONObject(newInfo);
            weatherOfCity.setCity(update_info, newInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //读取存档
    private String readSave(){
        String save;
        SharedPreferences pref = getSharedPreferences("save", MODE_PRIVATE);
        save = pref.getString("city", "false");
        if(save == "false")
        {
            save = readJsonFromFile();
        }
        return save;
    }
}
