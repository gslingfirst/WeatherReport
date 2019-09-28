package com.example.simpleweather;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


public class CityMenu extends AppCompatActivity {

    private String[] Province_list = {"北京","天津","上海","河北","河南","安徽","浙江","重庆","福建","甘肃","广东","广西","贵州","云南","内蒙古","江西","湖北","四川","宁夏","青海","山东","陕西","山西","新疆","西藏","台湾","海南","湖南","江苏","黑龙江","吉林","辽宁"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_menu);

        ActionBar actionbar = getSupportActionBar(); //隐藏默认标题栏
        if(actionbar != null)
        {
            actionbar.hide();
        }

        ImageView imageView = (ImageView) findViewById(R.id.back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent intent = getIntent();
        String cityName = intent.getStringExtra("CityName");
        Province[]  Provinces = new Province[32];
        for(int i = 0; i < 32; i ++)
        {
            Provinces[i] = new Province();
        }
        readInfoFromAssets(Provinces);
        for(int i = 0; i < 32; i ++)
        {
            if(Provinces[i].getName().equals(cityName))
            {
                set_city_name(Provinces[i]);
                break;
            }
        }
    }

    private void readInfoFromAssets(Province[] Provinces){
        InputStreamReader inputStreamReader;
        String result = new String();
        City[] cities = new City[36];
        for(int i = 0; i < 36; i ++)
        {
            cities[i] = new City();
        }
        try{
            inputStreamReader = new InputStreamReader(getAssets().open("CityID"),"UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            //StringBuilder stringBuilder = new StringBuilder();
            int i = 0,j = 0, flag = 0;
            while((line = bufferedReader.readLine()) != null)
            {
                if(line.length() < 5)
                {
                    if(flag == 1)
                    {
                        Provinces[i].setNum_of_city(j);
                        Provinces[i].setCityIDS(cities);
                        i++;
                        j = 0;
                    }
                    flag = 1;
                    Provinces[i].setName(Province_list[i]);
                    //Log.d("abd",String.valueOf(Provinces[i].num_of_city));
                }
                else
                {
                    cities[j].setInfo(line);
                    j++;
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
    }
    private void set_city_name(Province province){
        TableLayout table_of_city = (TableLayout) findViewById(R.id.table_of_city_2);
        int index = 0, flag = 0;
        for(int i = 0; i < table_of_city.getChildCount(); i++) {
            if(flag == 1)
            {
                break;
            }
            TableRow row = (TableRow) table_of_city.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                final Button chooseCity = (Button) row.getChildAt(j);
                if(province.getCityIDS()[index].getName().equals("NULL"))
                {
                    flag = 1;
                    break;
                }
                chooseCity.setText(province.getCityIDS()[index].getName());
                chooseCity.setTag(province.getCityIDS()[index].getId());
                index++;
                chooseCity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(CityMenu.this, MainActivity.class);
                        intent.putExtra("cityID",chooseCity.getTag().toString());
                        startActivity(intent);
                        //Log.d("abd", chooseCity.getTag().toString());
                    }
                });
            }
        }
    }
}

