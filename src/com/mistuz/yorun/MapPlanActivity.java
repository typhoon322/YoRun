package com.mistuz.yorun;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.map.Symbol.Stroke;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.mistuz.yorun.R;

public class MapPlanActivity extends Activity {

    BMapManager mBMapMan = null;
    MapView mMapView = null;
    GeoPoint currentPoint = new GeoPoint(0, 0);
    
    public static final String APP_KEY = "yoFM3IiWshI4C6lzm2GubSwZ";
    
    private GeoPoint lastPoint = new GeoPoint(0, 0) ;
    
    double lati,lon ;
    
    GraphicsOverlay graphicsOverlay  ;
    
    Symbol.Color lineColor ;
    
    int alpha = 180 , red = 0 , green = 255 , blue = 0;
    
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    
    private Handler handler = new Handler() {
        
        public void handleMessage(android.os.Message msg) {

            lineColor.alpha = alpha ;
            lineColor.red = red ;
            lineColor.green = green ;
            lineColor.blue = blue ;

            
//            Log.i("location", "--------------------------------------------------------------------------") ;
//            Log.i("location", "["+lastPoint.getLatitudeE6()+","+lastPoint.getLongitudeE6()+"]") ;
//            Log.v("location", "["+currentPoint.getLatitudeE6()+","+currentPoint.getLongitudeE6()+"]") ;
            
            graphicsOverlay.setData(drawLine(new GeoPoint[] {currentPoint,lastPoint},lineColor)) ;
            mMapView.refresh() ;
            mMapView.getController().animateTo(currentPoint);

            Toast.makeText(getApplicationContext(), "更新路径", Toast.LENGTH_SHORT).show() ;
            
            lastPoint.setLatitudeE6(currentPoint.getLatitudeE6()) ;
            lastPoint.setLongitudeE6(currentPoint.getLongitudeE6()) ;
            
        };
        
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        mBMapMan = new BMapManager(getApplication());
        mBMapMan.init(new DemoApplication.MyGeneralListener());
        // 注意：请在试用setContentView前初始化BMapManager对象，否则会报错
        setContentView(R.layout.activity_plan);
        mMapView = (MapView) findViewById(R.id.bmapsView);
        mMapView.setBuiltInZoomControls(true);
        // 设置启用内置的缩放控件
        MapController mMapController = mMapView.getController();
        // 得到mMapView的控制权,可以用它控制和驱动平移和缩放
        
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener( myListener );    //注册监听函数
        mLocationClient.setAccessKey(APP_KEY) ;
        LocationClientOption option = mLocationClient.getLocOption() ;
        option.setLocationMode(LocationMode.Device_Sensors);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度，默认值gcj02
        option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(false);//返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
        mLocationClient.setLocOption(option);
        double lat = getIntent().getDoubleExtra("lat", 22.562911);
        double lon = getIntent().getDoubleExtra("lon", 114.115379);
        currentPoint = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
        
        lastPoint.setLatitudeE6(currentPoint.getLatitudeE6()) ;
        lastPoint.setLongitudeE6(currentPoint.getLongitudeE6()) ;
        
        // 用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
        mMapController.setCenter(currentPoint);// 设置地图中心点
        mMapController.setZoom(18);// 设置地图zoom级别
        
        graphicsOverlay = new GraphicsOverlay(mMapView);
        mMapView.getOverlays().add(graphicsOverlay);
        
        lineColor = new Symbol().new Color(alpha, red,green , blue) ;
        
    }
    
    

    //114.111044,22.56215
    //114.110999,22.562317

//    private void showCurrentLocation() {
//        MyLocationOverlay myLocationOverlay = new MyLocationOverlay(mMapView);
//        LocationData locData = new LocationData();
//        // 手动将位置源置为天安门，在实际应用中，请使用百度定位SDK获取位置信息，要在SDK中显示一个位置，需要使用百度经纬度坐标（bd09ll）
//        locData.latitude = currentPoint.getLatitudeE6() / 1E6;
//        locData.longitude = currentPoint.getLongitudeE6() / 1E6;
//        locData.direction = 2.0f;
//        myLocationOverlay.setData(locData);
//        mMapView.getOverlays().add(myLocationOverlay);
//        mMapView.refresh();
//        mMapView.getController().animateTo( new GeoPoint((int) (locData.latitude * 1e6), (int) (locData.longitude * 1e6)));
//    }

    
    /**
     * 
     * @param pointList
     * @return
     */

    Geometry lineGeometry = new Geometry();
    Graphic lineGraphic = null ;
    public Graphic drawLine(GeoPoint[]  linePoints,Symbol.Color lineColor) {
        
        if(linePoints == null || linePoints.length<2) {
            return null ;
        }

            lineGeometry.setPolyLine(linePoints);
            // 设定样式
            Symbol lineSymbol = new Symbol();
            Symbol.Stroke stroke = new Stroke(3, lineSymbol.new Color(0xfffff) ) ;
//            lineSymbol.setLineSymbol(lineColor, 10);
            lineSymbol.setSurface(lineColor, 1, 10, stroke);
            // 生成Graphic对象
            lineGraphic = new Graphic(lineGeometry, lineSymbol);
            
            return lineGraphic;
    }
    

    @Override
    protected void onDestroy() {
        mMapView.destroy();
        if (mBMapMan != null) {
            mBMapMan.destroy();
            mBMapMan = null;
        }
        super.onDestroy();
        mLocationClient.stop() ;
        if(handler != null ) {
            handler.removeMessages(0) ;
            handler = null ;
        }
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        if (mBMapMan != null) {
            mBMapMan.stop();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        if (mBMapMan != null) {
            mBMapMan.start();
        }
        super.onResume();

        mLocationClient.start() ;
    }


    public class MyLocationListener implements BDLocationListener {
        @Override
       public void onReceiveLocation(BDLocation location) {
          if (location == null)
              return ;
          lati = location.getLatitude() ;
          lon = location.getLongitude() ;
          
          currentPoint.setLatitudeE6((int) ((lati) * 1E6)) ;
          currentPoint.setLongitudeE6( (int) (lon * 1E6)) ;
          
          if(handler != null) {
              handler.sendEmptyMessageDelayed(0, 100) ;
          }
     
        }

        @Override
        public void onReceivePoi(BDLocation arg0) {
            // TODO Auto-generated method stub
            
        }
        
    }
    
}
