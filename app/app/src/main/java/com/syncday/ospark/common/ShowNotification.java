package com.syncday.ospark.common;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.syncday.ospark.R;
import com.syncday.ospark.websocket.JWebSocketClientService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class ShowNotification extends Activity {

    private TextView info_tv,sendMessage;
    private MapView mapView;
    private Button confirm;
    private AMap aMap;
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //状态栏透明
        Window window =getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

        //动画
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        getWindow().setExitTransition(new Explode());

        setContentView(R.layout.activity_notification_show);

        intent = getIntent();
        initView(savedInstanceState);
        initService();
        initListener();
        getLocation();
    }

    private void initView(Bundle savedInstanceState){

        confirm = findViewById(R.id.notification_confirm);
        sendMessage = findViewById(R.id.notification_message);
        info_tv = findViewById(R.id.notification_info);
        mapView = findViewById(R.id.notification_map);

        info_tv.setText(intent.getStringExtra("info"));

        //高德地图
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mapView.getMap();
        //缩放大小
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
        double latitude = intent.getDoubleExtra("latitude",0);
        double longitude = intent.getDoubleExtra("longitude",0);

        if(latitude!=0&&longitude!=0){
            mapView.setVisibility(View.VISIBLE);
            Log.d("DEBUG","位置"+String.valueOf(latitude)+String.valueOf(longitude));
            LatLng latLng = new LatLng(latitude,longitude);
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
            aMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(),R.drawable.ic_location))));
        }
    }

    private void initListener(){
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendConfirm();
                onBackPressed();
            }
        });
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplication(),ChatActivity.class);
                intent1.putExtra("sender",intent.getStringExtra("from"));
                intent1.putExtra("nickname",intent.getStringExtra("nickname"));
                startActivity(intent1);
            }
        });
    }

    /**
     * 操作员确定收到服务信息，用户默认不发送
     */
    private void sendConfirm(){
        Host host = new Host(getApplicationContext());
        if(host.getAccountType().equals("operator")){
            JSONObject jsonObject =new JSONObject();
            String name = host.getPhoneNumber();
            try {
                jsonObject.put("type","notification");
                jsonObject.put("to",intent.getStringExtra("from"));
                jsonObject.put("from",host.getPhoneNumber());
                jsonObject.put("latitude",latitude);
                jsonObject.put("longitude",longitude);
                jsonObject.put("content","操作员"+name.substring(name.length()-4)+"已确认收到请求");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jWebSClientService.sendMsg(jsonObject.toString());
        }

    }

    //-----------------------------------------服务相关---------------------------------------------

    private JWebSocketClientService.JWebSocketClientBinder binder;
    private JWebSocketClientService jWebSClientService;

    /**
     * 连接service
     */
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            binder = (JWebSocketClientService.JWebSocketClientBinder) iBinder;
            jWebSClientService = binder.getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    /**
     * 绑定WebSocket服务
     */
    private void initService(){
        //不能再次启动服务，否则会产生两个进程
        Intent bindIntent = new Intent(getApplication(), JWebSocketClientService.class);
        getApplicationContext().bindService(bindIntent, connection, BIND_AUTO_CREATE); // 绑定服务
    }

    //---------------------------------------------定位相关-----------------------------------------
    private  AMapLocationClient mLocationClient;
    private String latitude="",longitude="";
    /**
     * 获取位置
     */
    private void getLocation() {
        mLocationClient = null;
        //声明定位回调监听器
        AMapLocationListener mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {
                        final Double mlatitude = amapLocation.getLatitude();//获取纬度
                        final Double mlongitude = amapLocation.getLongitude();//获取经度
                        //剪切地址经纬度到小数点后6位，误差为米
                        DecimalFormat df = new DecimalFormat("#.000000");
                        latitude = df.format(mlatitude);
                        longitude = df.format(mlongitude);
                    }else {
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError","location Error, ErrCode:"
                                + amapLocation.getErrorCode() + ", errInfo:"
                                + amapLocation.getErrorInfo());
                    }
                }
            }
        };
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplication());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //声明AMapLocationClientOption对象
        AMapLocationClientOption mLocationOption = null;
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        AMapLocationClientOption option = new AMapLocationClientOption();
        if(null != mLocationClient){
            mLocationClient.setLocationOption(option);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
            //高精度模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //给定位客户端对象设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            //启动定位
            mLocationClient.startLocation();
        }
    }
}
