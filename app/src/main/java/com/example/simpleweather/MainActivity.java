package com.example.simpleweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class MainActivity extends AppCompatActivity {

    public static final int UPDATE_INFO = 1;
    private GetInfo subp = new GetInfo();  //定义一个用于获取网络信息的子线程
    WeatherInfo weatherOfCity = new WeatherInfo();
    private boolean status = true;
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    //LocationClientOption option = new LocationClientOption();

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){  //用于与主程序交互
            switch (msg.what) {
                case UPDATE_INFO:
                    performData(weatherOfCity);
                    //getNotification();
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
            Log.d("abd", "in thread: " + cid);
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

    public String iTS(int x) //int to String
    {
        char[] c = {'0','1','2','3','4','5','6','7','8','9'};
        char i;
        String s = new String();
        int tem = x;
        while(tem > 0)
        {
            i = c[tem % 10];
            s = s + i;
            tem = tem/10;
        }
        //Log.d("abd",s);
        return s;
    }

    public String findCityId(String cityName, String district)
    {
        String line;
        String cityid = "101010100";
        try{
            InputStreamReader inputStreamReader = new InputStreamReader(getAssets().open("CityID"),"UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            if(cityName.compareTo("北京市") == 0)
            {
                Log.d("abd","city");
                while((line = bufferedReader.readLine()) != null)
                {
                    if(line.length() > 5)
                    {
                        String[] split_info = line.split(":");
                        if(district.compareTo(split_info[0] + "区") == 0)
                        {
                            Log.d("abd","district");
                            cityid = split_info[1];
                            break;
                        }
                    }
                }
            }
            else{
                while((line = bufferedReader.readLine()) != null)
                {
                    if(line.length() > 5)
                    {
                        String[] split_info = line.split(":");
                        if(cityName == split_info[0] + "市")
                        {
                            cityid = split_info[1];
                        }
                    }
                }
            }
            inputStreamReader.close();
            bufferedReader.close();
        }catch (UnsupportedEncodingException e){
            Log.d("abd","error 1");
            e.printStackTrace();
        }catch (IOException e){
            Log.d("abd","error 1");
            e.printStackTrace();
        }
        return cityid;
    }
    public class MyLocationListener extends BDAbstractLocationListener {
        public void onLocDiagnosticMessage(int i, int i1, String s) {
            super.onLocDiagnosticMessage(i, i1, s);
            Log.d("abd",iTS(i) + "--"+ iTS(i1) + "--" + s);
        }

        @Override
        public void onReceiveLocation(BDLocation location){
            //Log.d("abd",iTS(location.getLocType()) + "--"+ location.getLocTypeDescription());
            if (location != null && location.getLocType() != BDLocation.TypeServerError){
                //Log.d("abd","ReceiveLocation");
                String city = location.getCity();    //获取城市
                String district = location.getDistrict();    //获取区县
                Log.d("abd",city+"/"+district);
                String date = city+"/"+district;
                TextView updateDate = (TextView) findViewById(R.id.date);
                updateDate.setText(date);
                String adcode = location.getAdCode();
                String CityCode = location.getCityCode();
                String cityid;
                cityid = findCityId(city,district);
                subp.setCid(cityid);
                GetInfo chooseCityBack = new GetInfo();
                chooseCityBack.setCid(cityid);
                new Thread(chooseCityBack).start();
            }
            /*
            String addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            String city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息*/
            Log.d("abd","ReceiveLocationOut");
            mLocationClient.stop();
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

        mLocationClient = new LocationClient(getApplicationContext());//声明LocationClient类
        mLocationClient.registerLocationListener(myListener);//注册定位监听函数
        initLocation();
        String result = readSave();   //读取保存的信息
        updateInfo(result, weatherOfCity); //初始加载本地信息，更新界面
        performData(weatherOfCity);        //将获取的信息展示出来

        ScheduledExecutorService service = Executors.newScheduledThreadPool(2); //定时器
        long initialDelay = 0;
        long period = 30;
        service.scheduleWithFixedDelay(subp, initialDelay, period, TimeUnit.SECONDS); //增加定时任务，每30分钟刷新一次

        ActionBar actionbar = getSupportActionBar(); //隐藏默认标题栏
        if(actionbar != null)
        {
            actionbar.hide();
        }

        ImageView city = (ImageView) findViewById(R.id.cities);
        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.example.simpleweather.ProvinceMenu");
                startActivity(intent);
            }
        });

        ImageView curLoc = (ImageView) findViewById(R.id.location_0);
        curLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("abd","location start");
                RequestPermission();
            }
        });

    }
    void getNotification(){
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this,"1")
                .setContentTitle("天气更新")
                .setContentText("您已获取最新的天气信息")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                .build();
        manager.notify(1,notification);
    } //会议通知

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        //option.setOpenGps(true);
        //option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onDestroy() {
        //报存信息
        SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
        editor.putString("city", weatherOfCity.getInfo());
        editor.apply();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length > 0)
                {
                    for(int result : grantResults){
                        if(result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"must aggre all the permissions to start this function", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    Log.d("abd","RequestPermission");
                    requestLocation();
                }else {
                    Toast.makeText(this,"unknown error",Toast.LENGTH_SHORT).show();
                }
                break;
                default:
        }
    }

    //请求权限
    private void RequestPermission(){
        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        }else{
            requestLocation();
        }
    }
    private void requestLocation(){
        Log.d("abd","requestLocation");
        mLocationClient.start();
    }
    private void changeIcon(ImageView weaIcon, String type)
    {
        if(type.indexOf("晴") >= 0) {
            weaIcon.setBackgroundResource(R.drawable.sun_128);
        }
        if(type.indexOf("多云") >= 0) {
            weaIcon.setBackgroundResource(R.drawable.cloudyday_128);
        }
        if(type.indexOf("雨") >= 0) {
            weaIcon.setBackgroundResource(R.drawable.rainyday_128);
        }
        if(type.indexOf("雷") >= 0) {
            weaIcon.setBackgroundResource(R.drawable.thunderrain_128);
        }
        if(type.indexOf("雪") >= 0) {
            weaIcon.setBackgroundResource(R.drawable.snowyday_128);
        }
    }
    //展示未来7天的天气情况
    private void performForecast(WeatherInfo weatherOfCity){
        String get_date = weatherOfCity.getData().getForecast()[1].getYmd();
        String date = get_date.substring(5,7) + "月" + get_date.substring(8,10) + "日 " + weatherOfCity.getData().getForecast()[1].getWeek();
        TextView updateDate = (TextView) findViewById(R.id.first_date);
        updateDate.setText(date);
        String type = weatherOfCity.getData().getForecast()[1].getType();
        ImageView weaIcon = (ImageView) findViewById(R.id.first_wea);
        changeIcon(weaIcon, type);
        String range = weatherOfCity.getData().getForecast()[1].getLow().substring(3) + "--" + weatherOfCity.getData().getForecast()[1].getHigh().substring(3);
        TextView tem_range = (TextView) findViewById(R.id.first_tem);
        tem_range.setText(range);

        get_date = weatherOfCity.getData().getForecast()[2].getYmd();
        date = get_date.substring(5,7) + "月" + get_date.substring(8,10) + "日 " + weatherOfCity.getData().getForecast()[2].getWeek();
        updateDate = (TextView) findViewById(R.id.second_date);
        updateDate.setText(date);
        type = weatherOfCity.getData().getForecast()[2].getType();
        weaIcon = (ImageView) findViewById(R.id.second_wea);
        changeIcon(weaIcon, type);
        range = weatherOfCity.getData().getForecast()[2].getLow().substring(3) + "--" + weatherOfCity.getData().getForecast()[2].getHigh().substring(3);
        tem_range = (TextView) findViewById(R.id.second_tem);
        tem_range.setText(range);

        get_date = weatherOfCity.getData().getForecast()[3].getYmd();
        date = get_date.substring(5,7) + "月" + get_date.substring(8,10) + "日 " + weatherOfCity.getData().getForecast()[3].getWeek();
        updateDate = (TextView) findViewById(R.id.third_date);
        updateDate.setText(date);
        type = weatherOfCity.getData().getForecast()[3].getType();
        weaIcon = (ImageView) findViewById(R.id.third_wea);
        changeIcon(weaIcon, type);
        range = weatherOfCity.getData().getForecast()[3].getLow().substring(3) + "--" + weatherOfCity.getData().getForecast()[3].getHigh().substring(3);
        tem_range = (TextView) findViewById(R.id.third_tem);
        tem_range.setText(range);

        get_date = weatherOfCity.getData().getForecast()[4].getYmd();
        date = get_date.substring(5,7) + "月" + get_date.substring(8,10) + "日 " + weatherOfCity.getData().getForecast()[4].getWeek();
        updateDate = (TextView) findViewById(R.id.fourth_date);
        updateDate.setText(date);
        type = weatherOfCity.getData().getForecast()[4].getType();
        weaIcon = (ImageView) findViewById(R.id.fourth_wea);
        changeIcon(weaIcon, type);
        range = weatherOfCity.getData().getForecast()[4].getLow().substring(3) + "--" + weatherOfCity.getData().getForecast()[4].getHigh().substring(3);
        tem_range = (TextView) findViewById(R.id.fourth_tem);
        tem_range.setText(range);

        get_date = weatherOfCity.getData().getForecast()[5].getYmd();
        date = get_date.substring(5,7) + "月" + get_date.substring(8,10) + "日 " + weatherOfCity.getData().getForecast()[5].getWeek();
        updateDate = (TextView) findViewById(R.id.fifth_date);
        updateDate.setText(date);
        type = weatherOfCity.getData().getForecast()[5].getType();
        weaIcon = (ImageView) findViewById(R.id.fifth_wea);
        changeIcon(weaIcon, type);
        range = weatherOfCity.getData().getForecast()[5].getLow().substring(3) + "--" + weatherOfCity.getData().getForecast()[5].getHigh().substring(3);
        tem_range = (TextView) findViewById(R.id.fifth_tem);
        tem_range.setText(range);

        get_date = weatherOfCity.getData().getForecast()[6].getYmd();
        date = get_date.substring(5,7) + "月" + get_date.substring(8,10) + "日 " + weatherOfCity.getData().getForecast()[6].getWeek();
        updateDate = (TextView) findViewById(R.id.sixth_date);
        updateDate.setText(date);
        type = weatherOfCity.getData().getForecast()[6].getType();
        weaIcon = (ImageView) findViewById(R.id.sixth_wea);
        changeIcon(weaIcon, type);
        range = weatherOfCity.getData().getForecast()[6].getLow().substring(3) + "--" + weatherOfCity.getData().getForecast()[6].getHigh().substring(3);
        tem_range = (TextView) findViewById(R.id.sixth_tem);
        tem_range.setText(range);

        get_date = weatherOfCity.getData().getForecast()[7].getYmd();
        date = get_date.substring(5,7) + "月" + get_date.substring(8,10) + "日 " + weatherOfCity.getData().getForecast()[7].getWeek();
        updateDate = (TextView) findViewById(R.id.seventh_date);
        updateDate.setText(date);
        type = weatherOfCity.getData().getForecast()[7].getType();
        weaIcon = (ImageView) findViewById(R.id.seventh_wea);
        changeIcon(weaIcon, type);
        range = weatherOfCity.getData().getForecast()[7].getLow().substring(3) + "--" + weatherOfCity.getData().getForecast()[7].getHigh().substring(3);
        tem_range = (TextView) findViewById(R.id.second_tem);
        tem_range.setText(range);
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

        LinearLayout mainWindow = (LinearLayout) findViewById(R.id.main_window);
        ImageView weaIcon = (ImageView) findViewById(R.id.weather_icon);
        if(type.indexOf("晴") >= 0)
        {
            mainWindow.setBackgroundResource(R.drawable.sunny);
            weaIcon.setBackgroundResource(R.drawable.sunnyday);
        }
        if(type.indexOf("多云") >= 0)
        {
            mainWindow.setBackgroundResource(R.drawable.cloudy);
            weaIcon.setBackgroundResource(R.drawable.cloudyday);
        }
        if(type.indexOf("雨") >= 0)
        {
            mainWindow.setBackgroundResource(R.drawable.rain);
            weaIcon.setBackgroundResource(R.drawable.rainyday);
        }
        if(type.indexOf("雷") >= 0)
        {
            mainWindow.setBackgroundResource(R.drawable.thunder);
            weaIcon.setBackgroundResource(R.drawable.thunderrain);
        }
        if(type.indexOf("雪") >= 0)
        {
            mainWindow.setBackgroundResource(R.drawable.snow);
            weaIcon.setBackgroundResource(R.drawable.snowyday);
        }

        String range = weatherOfCity.getData().getForecast()[0].getLow().substring(3) + "--" + weatherOfCity.getData().getForecast()[0].getHigh().substring(3);
        TextView tem_range = (TextView) findViewById(R.id.tem_range);
        tem_range.setText(range);

        String quality = "空气质量: " + weatherOfCity.getData().getQuality();
        TextView air_quality = (TextView) findViewById(R.id.air_quality);
        air_quality.setText(quality);

        String wind = weatherOfCity.getData().getForecast()[0].getFx() + ": " + weatherOfCity.getData().getForecast()[0].getFl();
        TextView wind_condition = (TextView) findViewById(R.id.wind);
        wind_condition.setText(wind);

        performForecast(weatherOfCity);

        if(!status)
        {
            Toast.makeText(MainActivity.this,"抱歉，当前不提供您选择地区的天气信息！！！",Toast.LENGTH_SHORT).show();
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
