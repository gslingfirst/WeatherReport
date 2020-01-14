package com.example.simpleweather;

import android.content.Context;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class baiduLBS {
    private String cityName;
    private String cityId;

    private LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    LocationClientOption option = new LocationClientOption();

    public baiduLBS(Context context){
        cityName = "北京";
        cityId = "101010100";
        mLocationClient = new LocationClient(context);//声明LocationClient类
        mLocationClient.registerLocationListener(myListener);//注册定位监听函数
        option.setIsNeedAddress(true);
        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true
        mLocationClient.setLocOption(option);
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
    }

    public void startLBS() {
        mLocationClient.start();
    }

    public void stopLBS() {
        mLocationClient.stop();
    }
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            if (null != location && location.getLocType() != BDLocation.TypeServerError){
                String adcode = location.getAdCode();
                String CityCode = location.getCityCode();

            }
            String addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            String city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息
        }
    }
}

