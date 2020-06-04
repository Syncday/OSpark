package com.syncday.ospark.operator.nearby;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.syncday.ospark.R;
import com.syncday.ospark.operator.OperatorCarDetailActivity;
import com.syncday.ospark.common.ChatActivity;
import com.syncday.ospark.operator.OperatorPayActivity;
import com.syncday.ospark.operator.home.recyclerview.OnItemClickListener;
import com.syncday.ospark.common.Host;
import com.syncday.ospark.common.VolleyModel;
import com.syncday.ospark.bean.CarBean;
import com.syncday.ospark.operator.home.recyclerview.CustomRecyclerViewAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OperatorNearbyFragment extends Fragment {

    private View view;
    private MapView mapView = null;
    private AMap aMap;
    private CarBean carBean;
    private AMapLocationClient mLocationClient;

    private RecyclerView mRecycler;
    private List<CarBean.Car> mList = new ArrayList<>();
    private CustomRecyclerViewAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_operator_nearby,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView(savedInstanceState);

        initListener();

        getAddress();

    }

    /**
     * 初始化界面
     * @param savedInstanceState
     */
    private void initView(Bundle savedInstanceState){

        mapView = Objects.requireNonNull(getActivity()).findViewById(R.id.op_nearby_map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mapView.getMap();
        //缩放大小
        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));

        //定位
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(10000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW) ;
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。


        mRecycler = Objects.requireNonNull(getActivity()).findViewById(R.id.nearby_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(layoutManager);
        if (mAdapter == null) {
            mAdapter = new CustomRecyclerViewAdapter(getActivity(), mList);
            mRecycler.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }

    }


    //初始化监听器
    public void initListener(){
        mAdapter.setOnItemClickListener(new OnItemClickListener<CarBean.Car>() {
            @Override
            public void onItemClick(CarBean.Car bean, View view, int position) {
                switch (view.getId()){
                    case R.id.recycler_detail:
                        Intent intent =new Intent(getActivity(), OperatorCarDetailActivity.class);
                        intent.putExtra("car",bean.getParking_car());
                        intent.putExtra("time",bean.getParking_time());
                        intent.putExtra("latitude",bean.getParking_latitude());
                        intent.putExtra("longitude",bean.getParking_longitude());
                        intent.putExtra("address",bean.getParking_address());
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                        break;
                    case R.id.recycler_message:
                        Intent intent1 = new Intent(getActivity(), ChatActivity.class);
                        intent1.putExtra("sender",bean.getParking_user());
                        intent1.putExtra("nickname",bean.getParking_car());
                        startActivity(intent1);
                        break;
                    case R.id.recycler_pay:
                        Intent intent3 = new Intent(getActivity(), OperatorPayActivity.class);
                        intent3.putExtra("car",bean.getParking_car());
                        startActivity(intent3);
                        break;
                }
            }
        });

    }


    /**
     * 获取当前位置
     */
    private void getAddress(){

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
                        final  Double mlongitude = amapLocation.getLongitude();//获取经度

                        setNearbyCar(mlatitude,mlongitude);


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
        mLocationClient = new AMapLocationClient(getActivity());
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
            //定位间隔10s
            mLocationOption.setInterval(10000);
            //给定位客户端对象设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            //启动定位
            mLocationClient.startLocation();
        }
    }

    /**
     * 获取附近车辆
     */
    private void setNearbyCar(Double latitude,Double longitude){

        final Host host =new Host(Objects.requireNonNull(getActivity()));
        String phone_number = host.getPhoneNumber();
        String token = host.getToken();
        RequestQueue queue = VolleyModel.getInstance(getActivity().getApplicationContext()).
                getRequestQueue();
        //请求地址以及json格式的请求内容
        String url = Host.DOMAIN +"/get_nearby";

        final JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("phone_number",phone_number);
            jsonObject.put("token",token);
            jsonObject.put("latitude",latitude);
            jsonObject.put("longitude",longitude);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // @return {   status: ,car[]: ,info:}
                        String status="",info="";
                        try {
                            status = response.getString("status");
                            info = response.getString("info");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Gson gson = new Gson();
                        carBean = gson.fromJson(response.toString(),CarBean.class);

                        switch (status){
                            case "0":
                                Toast.makeText(getActivity(),info,Toast.LENGTH_LONG).show();
                                break;
                            case "1":
                                mList.clear();
                                mList.addAll(carBean.getCars());
                                mAdapter.notifyDataSetChanged();
                                setMarker();
                                break;
                                default:
                                    if(!info.equals("")){
                                        Toast.makeText(getActivity(),info,Toast.LENGTH_LONG).show();
                                    }
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),"无网络连接，或服务器无响应",Toast.LENGTH_LONG).show();
                    }
                });
        VolleyModel.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
    }

    /**
     * 设置地图标记点
     */
    private void setMarker(){

        for(CarBean.Car car: mList){
            LatLng latLng = new LatLng(car.getParking_latitude(),car.getParking_longitude());
            aMap.addMarker(new MarkerOptions().position(latLng).title(car.getParking_car()));
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mLocationClient.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
        //恢复定位
        mLocationClient.startLocation();
    }
    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
        //停止定位
        mLocationClient.stopLocation();
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }

}
