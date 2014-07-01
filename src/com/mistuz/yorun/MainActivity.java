package com.mistuz.yorun;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.mistuz.yorun.R;

public class MainActivity extends Activity {
    
    public static final String APP_KEY = "yoFM3IiWshI4C6lzm2GubSwZ";
    TextView textview ;
    Button button ;

    Handler handler = new Handler() {
        
        public void handleMessage(android.os.Message msg) {
            textview.setText("当前城市： "+GlobalVar.CITY) ;
        };
        
    } ;
    
    double latitude,longitude ;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        textview = (TextView) findViewById(R.id.textview) ;
        button = (Button) findViewById(R.id.button) ;
        
        button.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                Intent intent = new Intent(MainActivity.this, MapPlanActivity.class) ;
                Intent intent = new Intent(MainActivity.this, MapPlanActivity.class) ;
                intent.putExtra("lon", longitude) ;
                intent.putExtra("lat", latitude) ;
                startActivity(intent) ;
            }
        });
        
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener( myListener );    //注册监听函数
        mLocationClient.setAccessKey(APP_KEY) ;
        
    }
    
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        getCityName() ;
    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }


    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    
    public void getCityName() {
        new Thread() {
            public void run() {
                Log.i("baidu-map", "start thread ");
                LocateNow();

            };

        }.start();
    }
    
    private void LocateNow() {
        
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Device_Sensors);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度，默认值gcj02
        option.setScanSpan(10000);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
        mLocationClient.setLocOption(option);
        
        mLocationClient.start() ;
        if (mLocationClient != null && mLocationClient.isStarted()) {
            Log.d("baidumap-info", "locClient is start...........");
            mLocationClient.requestLocation();
        }
        else {

            Log.d("baidumap-info", "locClient is null or not started");
        }
    }
    
    public class MyLocationListener implements BDLocationListener {
        @Override
       public void onReceiveLocation(BDLocation location) {
          if (location == null)
              return ;
          StringBuffer sb = new StringBuffer(256);
          sb.append("time : ");
          sb.append(location.getTime());
          sb.append("\nerror code : ");
          sb.append(location.getLocType());
          sb.append("\nlatitude : ");
          sb.append(location.getLatitude());
          sb.append("\nlontitude : ");
          sb.append(location.getLongitude());
          sb.append("\nradius : ");
          sb.append(location.getRadius());
          sb.append("\ncity : ");
          sb.append(location.getCity());
          sb.append("\ncitycode : ");
          sb.append(location.getCityCode());
          sb.append("\nlocationType : ");
          sb.append(location.getLocType());
          sb.append("\nSpeed : ");
          sb.append(location.getSpeed()) ;
          sb.append("\nSatellite : ");
          sb.append(location.getSatelliteNumber()) ;
          
          if (location.getLocType() == BDLocation.TypeGpsLocation){
               sb.append("\nspeed : ");
               sb.append(location.getSpeed());
               sb.append("\nsatellite : ");
               sb.append(location.getSatelliteNumber());
               } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
               sb.append("\naddr : ");
               sb.append(location.getAddrStr());
            } 
          
          textview.setText(sb.toString());
          
          latitude = location.getLatitude() ;
          longitude = location.getLongitude() ;
          
          GlobalVar.CITY = location.getCity() ;
     
        }

        @Override
        public void onReceivePoi(BDLocation arg0) {
            // TODO Auto-generated method stub
            
        }
        
    }


}
