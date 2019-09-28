package com.example.simpleweather;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

public class ProvinceMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_province_menu);

        ActionBar actionbar = getSupportActionBar(); //隐藏默认标题栏
        if(actionbar != null)
        {
            actionbar.hide();
        }

        ImageView back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TableLayout table_of_city = (TableLayout) findViewById(R.id.table_of_city);

        for(int i = 0; i < table_of_city.getChildCount(); i++)
        {
            TableRow row = (TableRow) table_of_city.getChildAt(i);
            for(int j = 0; j < row.getChildCount(); j++)
            {
                final Button chooseCity = (Button) row.getChildAt(j);
                chooseCity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ProvinceMenu.this, CityMenu.class);
                        intent.putExtra("CityName",chooseCity.getText());
                        startActivity(intent);
                    }
                });
            }
        }
    }
}

