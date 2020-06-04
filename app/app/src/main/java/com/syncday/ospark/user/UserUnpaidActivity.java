package com.syncday.ospark.user;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.syncday.ospark.R;
import com.syncday.ospark.adapter.BillRecyclerViewAdapter;
import com.syncday.ospark.adapter.OnItemEventListener;
import com.syncday.ospark.bean.BillBean;
import com.syncday.ospark.common.Host;
import com.syncday.ospark.common.VolleyModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserUnpaidActivity extends AppCompatActivity {

    private RecyclerView mRecycler;
    private BillRecyclerViewAdapter mAdapter;
    private List<BillBean.Bill> mList = new ArrayList<>();
    private static final int SDK_PAY_FLAG = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //支付宝沙箱环境
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_unpaid);

        initView();
        initListener();
        getUnpaid();

    }

    private void initView(){
        Toolbar toolbar = findViewById(R.id.user_bill_unpaid_toolbar);
        mRecycler = findViewById(R.id.user_bill_unpaid_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(layoutManager);
        if (mAdapter == null) {
            mAdapter = new BillRecyclerViewAdapter(this, mList);
            mRecycler.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initListener(){

        mAdapter.setOnItemEventListener(new OnItemEventListener<BillBean.Bill>() {
            @Override
            public void onItemClick(BillBean.Bill bean, View view, int position) {
                getOrder(bean.getBill_id());
            }

            @Override
            public void onItemLongClick(BillBean.Bill bean, View view, int position) {

            }
        });
    }

    /**
     * 获取账单
     */
    private  void getUnpaid(){
        final Host host =new Host(this);
        String phone_number = host.getPhoneNumber();
        String token = host.getToken();
        RequestQueue queue = VolleyModel.getInstance(this.getApplicationContext()).
                getRequestQueue();
        //请求地址以及json格式的请求内容
        String url = Host.DOMAIN +"/get_unpaid";
        final JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("phone_number",phone_number);
            jsonObject.put("token",token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // @return { count: , BillBean:[] }

                        Gson gson = new Gson();
                        BillBean billBean = gson.fromJson(response.toString(),BillBean.class);
                        mList.clear();
                        mList.addAll(billBean.getBills());
                        mAdapter.notifyDataSetChanged();


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplication(),"无法获取未支付账单",Toast.LENGTH_LONG).show();
                        Log.d("DEBUG","get_paid_error:"+error.toString());
                    }
                });
        VolleyModel.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    /**
     * 获取账单信息并调用支付
     */
    private void getOrder(final String id){
        final Host host =new Host(this);
        String phone_number = host.getPhoneNumber();
        String token = host.getToken();
        RequestQueue queue = VolleyModel.getInstance(this.getApplicationContext()).
                getRequestQueue();
        //请求地址以及json格式的请求内容
        String url = Host.DOMAIN +"/get_alipay_order";
        final JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("phone_number",phone_number);
            jsonObject.put("token",token);
            jsonObject.put("id",id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // @return { status: , order: , info: , order_id: }

                        String status= "",order="",info="";
                        try {
                            status = response.getString("status");
                            info = response.getString("info");
                            order = response.getString("order");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //调用支付
                        if(status.equals("1")){
                            final String orderInfo = order;
                            final Runnable payRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    PayTask alipay = new PayTask(UserUnpaidActivity.this);
                                    Map<String, String> result = alipay.payV2(orderInfo, true);
                                    Log.i("msp", result.toString());
                                    Message msg = new Message();
                                    msg.what = SDK_PAY_FLAG;
                                    msg.obj = result;
                                    mHandler.sendMessage(msg);
                                }
                            };
                            // 必须异步调用
                            Thread payThread = new Thread(payRunnable);
                            payThread.start();
                        }else {
                            Toast.makeText(getApplication(),info,Toast.LENGTH_SHORT).show();
                            getUnpaid();
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplication(),"无法获取未支付账单",Toast.LENGTH_LONG).show();
                        Log.d("DEBUG","get_paid_error:"+error.toString());
                    }
                });
        VolleyModel.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    /**
     * 处理支付结果
     */
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        String[] tmp1 = payResult.toString().split("out_trade_no\":\"");
                        String[] tmp2 = tmp1[1].split("\",");
                        Log.d("DEBUG","handler");
                        verifyOrder(tmp2[0]);
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(getApplication(),"支付失败",Toast.LENGTH_SHORT).show();
                        getUnpaid();
                    }
                    break;
                }
            }
        }
    };

    /**
     * 发送数据给后端验证
     */
    private void verifyOrder(String order_id){
        if(order_id==null || order_id.equals("")){
            return;
        }
        RequestQueue queue = VolleyModel.getInstance(this.getApplicationContext()).
                getRequestQueue();
        //请求地址以及json格式的请求内容
        String url = Host.DOMAIN +"/get_verify_order";
        final JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("order_id",order_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // @return { status: , info: }
                        String status= "",info="";
                        try {
                            status = response.getString("status");
                            info = response.getString("info");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getUnpaid();
                        Toast.makeText(getApplication(),info,Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplication(),"无法验证支付账单",Toast.LENGTH_LONG).show();
                    }
                });
        VolleyModel.getInstance(this).addToRequestQueue(jsObjRequest);
    }
}
