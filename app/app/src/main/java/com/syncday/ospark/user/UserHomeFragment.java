package com.syncday.ospark.user;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.syncday.ospark.R;
import com.syncday.ospark.adapter.PriceListRecyclerViewAdapter;
import com.syncday.ospark.bean.PriceListBean;
import com.syncday.ospark.common.ChatActivity;
import com.syncday.ospark.common.Host;
import com.syncday.ospark.common.VolleyModel;
import com.syncday.ospark.websocket.JWebSocketClientService;
import com.syncday.ospark.websocket.JWebsocketClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.BIND_AUTO_CREATE;

public class UserHomeFragment extends Fragment {

    private View view;
    private LinearLayout price_layout;
    private TableLayout price_table;
    private TextView price_tv,address_tv,time_tv;
    private RecyclerView mRecycler;
    private Button stop, send;
    private Host host;
    private  AMapLocationClient mLocationClient;

    private String operator,latitude,longitude,car;

    private List<PriceListBean.priceList> mList = new ArrayList<>();
    private PriceListRecyclerViewAdapter mAdapter;

    private JWebsocketClient client;
    private JWebSocketClientService.JWebSocketClientBinder binder;
    private JWebSocketClientService jWebSClientService;
    /**
     * 轮询用户的服务状态和账单
     */
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(runnable, 10000);
            getOrder();
        }
    };
    /**
     * 连接service
     */
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            binder = (JWebSocketClientService.JWebSocketClientBinder) iBinder;
            jWebSClientService = binder.getService();
            client = jWebSClientService.client;

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_home, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        host = new Host(Objects.requireNonNull(getActivity()));

        initView();

        initListener();

        initService();

        getLocation();

    }

    /**
     * 启动时进行轮询
     */
    @Override
    public void onResume() {
        super.onResume();
        getAndSetPriceList();
        handler.postDelayed(runnable, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
        Log.d("DEBUG", "服务状态获取已暂停");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            Objects.requireNonNull(getActivity()).unbindService(connection);
            if(mLocationClient!=null){
                mLocationClient.onDestroy();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 初始化界面
     */
    private void initView() {
        price_layout = getActivity().findViewById(R.id.user_home_price_layout);
        price_tv = getActivity().findViewById(R.id.user_home_price);
        mRecycler = getActivity().findViewById(R.id.user_home_recyclerView);
        stop = getActivity().findViewById(R.id.user_home_stop_service);
        send = getActivity().findViewById(R.id.user_home_send_message);
        address_tv = getActivity().findViewById(R.id.user_home_address);
        time_tv =getActivity().findViewById(R.id.user_home_time);

        price_layout.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(layoutManager);
        if (mAdapter == null) {
            mAdapter = new PriceListRecyclerViewAdapter(getActivity(), mList);
            mRecycler.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }

    }

    /**
     * 注册监听器
     */
    private void initListener() {
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("终止停车服务？");
                builder.setMessage("提示:\n" +
                        "\t\t建议打开位置服务，可以方便操作员查看您当前的位置。确认后请等待工作人员回复。");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface ensure_dialog, int which) {
                        stopService();
                        ensure_dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface ensure_dialog, int which) {
                        ensure_dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("sender",operator);
                intent.putExtra("nickname","操作员"+operator.substring(operator.length()-4));
                startActivity(intent);
            }
        });
    }

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
            //给定位客户端对象设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            //启动定位
            mLocationClient.startLocation();
        }
    }

    /**
     * 获取价格表并显示
     */
    private void getAndSetPriceList() {
        RequestQueue queue = VolleyModel.getInstance(getActivity()).
                getRequestQueue();
        //请求地址以及json格式的请求内容
        String url = Host.DOMAIN + "/get_price_list";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone_number", host.getPhoneNumber());
            jsonObject.put("token", host.getToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // @return {   price_list:[]   }
                        Gson gson = new Gson();
                        PriceListBean priceListBean = gson.fromJson(response.toString(), PriceListBean.class);
                        mList.clear();
                        mList.addAll(priceListBean.getPriceList());
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "无网络连接，或服务器无响应", Toast.LENGTH_LONG).show();
                        Log.d("DEBUG", "get_price_list_error:" + error.toString());
                    }
                });
        VolleyModel.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
    }

    /**
     * 获取服务相关信息
     */
    private void getOrder() {
        Log.d("DEBUG", "获取服务状态中");
        RequestQueue queue = VolleyModel.getInstance(getActivity()).
                getRequestQueue();
        //请求地址以及json格式的请求内容
        String url = Host.DOMAIN + "/get_service_status";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone_number", host.getPhoneNumber());
            jsonObject.put("token", host.getToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // @return {   status: ,price:   }
                        String status = "", price = "",address="",time="";
                        try {
                            status = response.getString("status");
                            price = response.getString("price");
                            operator = response.getString("operator");
                            car = response.getString("car");
                            address = response.getString("address");
                            time = response.getString("time");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (status.equals("1")) {
                            price_layout.setVisibility(View.VISIBLE);
                            stop.setVisibility(View.VISIBLE);
                            send.setVisibility(View.VISIBLE);
                            price = "￥" + price;
                            price_tv.setText(price);
                            address_tv.setText(address);
                            time = time+"分钟";
                            time_tv.setText(time);
                            Log.d("DEBUG", "获取成功");
                        } else {
                            price_layout.setVisibility(View.GONE);
                            stop.setVisibility(View.GONE);
                            send.setVisibility(View.GONE);
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "无网络连接，或服务器无响应", Toast.LENGTH_LONG).show();
                        Log.d("DEBUG", "get_price_list_error:" + error.toString());
                    }
                });
        VolleyModel.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
    }


    //-----------------------------------------WebSocket服务相关---------------------------------------------

    /**
     * 通过WebSocket发送通知
     */
    private void stopService() {
        if (client != null && client.isOpen()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("type", "notification");
                jsonObject.put("from",host.getPhoneNumber());
                jsonObject.put("to",operator);
                jsonObject.put("latitude",latitude!=null?latitude:"");
                jsonObject.put("longitude",longitude!=null?longitude:"");
                jsonObject.put("content",car+"请求终止停车服务");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("DEBUG",jsonObject.toString());
            client.send(jsonObject.toString());
            Toast.makeText(getActivity(),"已发送，请等待回复",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "未连接到服务器，请稍后重试", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 绑定WebSocket服务
     */
    private void initService() {
        //不能再次启动服务，否则会产生两个进程
        Intent bindIntent = new Intent(getActivity(), JWebSocketClientService.class);
        Objects.requireNonNull(getActivity()).bindService(bindIntent, connection, BIND_AUTO_CREATE); // 绑定服务
    }

}
