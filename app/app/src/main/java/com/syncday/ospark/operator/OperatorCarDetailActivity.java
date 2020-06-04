package com.syncday.ospark.operator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.Fade;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.syncday.ospark.R;

public class OperatorCarDetailActivity extends Activity {

    private MapView mapView;
    private  AMap aMap;

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
        //getWindow().setEnterTransition(new Slide());
        getWindow().setExitTransition(new Fade());

        setContentView(R.layout.activity_car_detail);

        initView(savedInstanceState);

    }

    private void initView(Bundle savedInstanceState){
        Intent intent =getIntent();
        String car = intent.getStringExtra("car");
        String time = intent.getStringExtra("time");
        String address = intent.getStringExtra("address");
        double latitude = intent.getDoubleExtra("latitude",0);
        double longitude = intent.getDoubleExtra("longitude",0);

        mapView = findViewById(R.id.recycler_detail_map);
        TextView car_tv = findViewById(R.id.recycler_detail_car);
        TextView time_tv = findViewById(R.id.recycler_detail_time);
        TextView address_tv = findViewById(R.id.recycler_detail_address);
        car_tv.append(car);
        time_tv.append(time);
        address_tv.append(address);

        mapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mapView.getMap();
        aMap.getUiSettings().setZoomControlsEnabled(false);
        //marker以及地图中心显示位置
        LatLng latLng = new LatLng(latitude,longitude);
        aMap.addMarker(new MarkerOptions().position(latLng).title(address));
        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng,20,60,0)));


    }

    /**
     * 重写屏幕点击方法，点击屏幕便退出活动
     * @param event
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //点击屏幕
        if (event.getAction() == MotionEvent.ACTION_UP) {
           onBackPressed();
        }
        return super.dispatchTouchEvent(event);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }
}
