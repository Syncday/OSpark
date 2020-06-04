package com.syncday.ospark.operator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.pxy.LicensePlateView;
import com.syncday.ospark.LauncherActivity;
import com.syncday.ospark.R;
import com.syncday.ospark.common.Host;
import com.syncday.ospark.common.VolleyModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class OperatorAddCarActivity extends AppCompatActivity  implements LicensePlateView.InputListener {

    Host host;

    private MapView mapView;
    private AMap aMap;
    private TextView add_address;
    private TextView add_time;
    private TextView car_textView;
    private TextView notice;
    AMapLocationClient mLocationClient;
    RelativeLayout map_container;
    private Toolbar toolbar;
    private Button addCar;
    //自定义键盘
    LicensePlateView keyboard_view;
    RelativeLayout keyboard_container;

    //发给服务器的数据
    private String car,operator,token,latitude,longitude,address;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        host = new Host(this);

        initView(savedInstanceState);
        setTime();
        setAddress();

    }

    private void initView( Bundle savedInstanceState){

        add_time = findViewById(R.id.add_car_time_textView);
        add_address = findViewById(R.id.add_car_address_textView);
        car_textView = findViewById(R.id.add_car_car_editText);
        notice = findViewById(R.id.add_car_notice);
        mapView = (MapView) findViewById(R.id.map);
        keyboard_view = findViewById(R.id.add_car_custom_keyboard);
        keyboard_container = findViewById(R.id.add_car_custom_keyboard_container);
        map_container = findViewById(R.id.add_car_map_container);
        toolbar = findViewById(R.id.add_car_toolbar);
        addCar = findViewById(R.id.add_car_submit_button);
        //隐藏自定义键盘
        keyboard_container.setVisibility(View.INVISIBLE);

        mapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mapView.getMap();
        //缩放大小
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));


        //自定义键盘相关
        //设置监听，实现对应的方法，方便在当前页面实现自己的逻辑
        keyboard_view.setInputListener(this);
        //设置父布局作为自定义键盘的容器
        keyboard_view.setKeyboardContainerLayout(keyboard_container);
        //输入七位和八位车牌号码的方法
        //mPlateView.showLastView();
        keyboard_view.hideLastView();

        car_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map_container.setVisibility(View.INVISIBLE);
                keyboard_container.setVisibility(View.VISIBLE);
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCar();
            }
        });

    }


    private void setTime(){
        RequestQueue queue = VolleyModel.getInstance(this.getApplicationContext()).
                getRequestQueue();
        //请求地址以及json格式的请求内容
        String url = Host.DOMAIN +"/get_time";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // @return {   time:    }
                        String time="";
                        try {
                            time = response.getString("time");
                            add_time.setText(time);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplication(),"未知错误",Toast.LENGTH_LONG).show();
                        Log.d("DEBUG","get_time_error:"+error.toString());
                    }
                });
        VolleyModel.getInstance(this).addToRequestQueue(jsObjRequest);

    }

    private void setAddress(){

        //高德定位蓝点显示
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW) ;
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

        //获取定位数据
        //声明AMapLocationClient类对象
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

                        //将经纬度转换为地址
                        GeocodeSearch geocoderSearch = new GeocodeSearch(getApplication());
                        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener(){
                            @Override
                            public void onGeocodeSearched(GeocodeResult result, int rCode) {
                                // TODO Auto-generated method stub
                            }
                            @Override
                            public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
                                if (rCode == 1000) {
                                    if (result != null && result.getRegeocodeAddress() != null
                                            && result.getRegeocodeAddress().getFormatAddress() != null) {
                                        address = result.getRegeocodeAddress().getFormatAddress();
                                        //Log.d("DEBUG","地址："+address);
                                        //把地址写到界面上
                                        add_address.setText(address);
                                    }
                                }
                            }
                        });
                        LatLonPoint lp = new LatLonPoint(mlatitude,mlongitude);
                        RegeocodeQuery query = new RegeocodeQuery(lp, 200,GeocodeSearch.AMAP);
                        geocoderSearch.getFromLocationAsyn(query);
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
        mLocationClient = new AMapLocationClient(getApplicationContext());
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

    private void addCar(){

        //验证车辆的信息是否输入完成
        if(car == null){
            Toast.makeText(getApplication(),"请输入车牌号",Toast.LENGTH_LONG).show();
            return;
        }
        if(address == null){
            Toast.makeText(getApplication(),"当前位置获取失败",Toast.LENGTH_LONG).show();
            return;
        }

        //补充操作员的信息
        operator = host.getPhoneNumber();
        token = host.getToken();

        String url = host.DOMAIN+"/add_car";
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("phone_number",operator);
            jsonObject.put("token",token);
            jsonObject.put("car",car);
            jsonObject.put("latitude",latitude);
            jsonObject.put("longitude",longitude);
            jsonObject.put("address",address);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // @return {   status: ,info:}
                        //status:   -1,0,1
                        //info:
                        String status="",token="",account_type="",info="";

                        try {
                            status = response.getString("status");
                            info = response.getString("info");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        switch (status){
                            //验证用户失败
                            case "-1":
                                Toast.makeText(getApplication(),info,Toast.LENGTH_LONG).show();
                                host.setAccountType(null);
                                host.setToken(null);
                                host.setPhoneNumber(null);
                                Intent intent =new Intent(getApplication(), LauncherActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                break;
                            case "0":
                                Toast.makeText(getApplication(),info,Toast.LENGTH_LONG).show();
                                break;
                            case "1":
                                Toast.makeText(getApplication(),info,Toast.LENGTH_LONG).show();
                                finish();
                                break;
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplication(),"无网络连接，或服务器无响应",Toast.LENGTH_LONG).show();
                    }
                });
        VolleyModel.getInstance(this).addToRequestQueue(jsObjRequest);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
        mLocationClient.stopLocation();//停止定位
        mLocationClient.onDestroy();//销毁定位客户端。
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mapView.onSaveInstanceState(outState);
    }


    /**
     * 键盘输入完成时调用
     * @param s 车牌号
     */
    @Override
    public void inputComplete(String s) {
        car_textView.setText(s);
        car_textView.setTextColor(0xFF515151);
        keyboard_container.setVisibility(View.GONE);
        map_container.setVisibility(View.VISIBLE);
        notice.setVisibility(View.GONE);
        car = s;
    }

    @Override
    public void deleteContent() {
       car = null;
    }
}
