package com.syncday.ospark.operator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;
import com.syncday.ospark.R;
import com.syncday.ospark.bean.PayBean;
import com.syncday.ospark.common.Host;
import com.syncday.ospark.common.VolleyModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OperatorPayActivity extends AppCompatActivity {
    private  String car;
    private TextView car_tv,time_tv,address_tv,price_tv,pay_platform;
    private Button selected;
    private Toolbar toolbar;
    private RadioGroup radioGroup;
    NetworkImageView imageView;
    ImageLoader mImageLoader;
    PayBean payBean = new PayBean();
    List<PayBean.Pay> pay = new ArrayList<>();
    private Integer index=0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_pay);
        Intent intent =getIntent();
        car = intent.getStringExtra("car");

        initView();
        initListener();
        getAndSetInfo();

    }

    private void  initView(){
        car_tv = findViewById(R.id.op_pay_parking_car);
        address_tv = findViewById(R.id.op_pay_parking_address);
        time_tv = findViewById(R.id.op_pay_parking_time);
        price_tv =findViewById(R.id.op_pay_price);
        radioGroup = findViewById(R.id.op_pay_radioGroup);
        selected =findViewById(R.id.op_pay_selected);
        toolbar = findViewById(R.id.op_pay_toobar);


        car_tv.setText(car);
    }

    /**
     * 注册监听器
     */
    private void initListener(){
        selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.op_pay_by_scan:
                        showScanDialog();
                        break;
                    case R.id.op_pay_by_cash:
                        payByCash();
                        break;
                    case R.id.op_pay_not:
                        stopService();
                }
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getAndSetInfo(){
        Host host = new Host(this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone_number",host.getPhoneNumber());
            jsonObject.put("token",host.getToken());
            jsonObject.put("car",car);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue queue = VolleyModel.getInstance(this.getApplicationContext()).
                getRequestQueue();
        //请求地址以及json格式的请求内容
        String url = Host.DOMAIN +"/get_pay_info";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // @return {   time: ,address: ,price:   ,pay[]}
                        Gson gson = new Gson();
                        payBean = gson.fromJson(response.toString(),PayBean.class);
                        Log.d("DEBUG",payBean.toString());
                        pay.addAll(payBean.getPays());
                        address_tv.append(payBean.getAddress());
                        time_tv.append(payBean.getTime());
                        price_tv.setText(payBean.getPrice());

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplication(),"无法连接到服务器",Toast.LENGTH_LONG).show();
                        Log.d("DEBUG","get_pay_info_error:"+error.toString());
                    }
                });
        VolleyModel.getInstance(getApplication()).addToRequestQueue(jsObjRequest);
    }

    /**
     * 显示扫码的二维码
     */
    private void showScanDialog(){
        //布局文件转换为View对象
        LayoutInflater inflater = LayoutInflater.from(this);
        final RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.dialog__op_pay_weali, null);
        // 新建对话框对象
        final Dialog dialog = new AlertDialog.Builder(this).create();
        dialog.show();
        WindowManager manager = getWindowManager();
        Display d = manager.getDefaultDisplay();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.height = (int) (d.getHeight() * 0.6);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setContentView(layout);

        RelativeLayout pay_laout = layout.findViewById(R.id.op_pay_layout);
        final Button ensuer = layout.findViewById(R.id.op_pay_by_scan_ensure);
        //在线图片显示控件
        imageView = (NetworkImageView) layout.findViewById(R.id.op_pay_weali);
        mImageLoader = VolleyModel.getInstance(this).getImageLoader();
        pay_platform = layout.findViewById(R.id.op_pay_weali_platform);

        if(pay.size()>0){
            imageView.setImageUrl(pay.get(0).getPlatform_url(), mImageLoader);
            pay_platform.setText(pay.get(0).getPlatform_app());
        }

        //切换二维码
        pay_laout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = index+1;
                //重置支付二维码的位置
                if(index >= pay.size()){
                    index =0;
                }
                imageView.setImageUrl(pay.get(index).getPlatform_url(), mImageLoader);
                pay_platform.setText(pay.get(index).getPlatform_app());
            }
        });
        //确认已支付
        ensuer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OperatorPayActivity.this);
                builder.setMessage("确认已支付？");
                builder.setTitle("提示");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface ensure_dialog, int which) {
                        ensuerPay("scan");
                        ensure_dialog.dismiss();
                        dialog.dismiss();
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
    }

    /**
     * 请求现金支付
     */
    private  void payByCash(){
        AlertDialog.Builder builder = new AlertDialog.Builder(OperatorPayActivity.this);
        builder.setMessage("确认已支付？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface ensure_dialog, int which) {
                ensuerPay("cash");
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

    /**
     * 请求终止服务
     */
    private void stopService(){
        AlertDialog.Builder builder = new AlertDialog.Builder(OperatorPayActivity.this);
        builder.setMessage("终止该车辆的服务？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface ensure_dialog, int which) {
                ensuerPay("not");
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

    /**
     * 确认支付
     */
    private void ensuerPay(String pay_by){
        Host host = new Host(this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone_number",host.getPhoneNumber());
            jsonObject.put("token",host.getToken());
            jsonObject.put("car",car);
            jsonObject.put("pay_by",pay_by);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue queue = VolleyModel.getInstance(this.getApplicationContext()).
                getRequestQueue();
        //请求地址以及json格式的请求内容
        String url = Host.DOMAIN +"/operator_get_to_pay";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            String info = response.getString("info");
                            Toast.makeText(getApplication(),info,Toast.LENGTH_LONG).show();
                            if(status.equals("1")){
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplication(),"无法连接到服务器,请重试",Toast.LENGTH_LONG).show();
                        Log.d("DEBUG","get_pay_by_error:"+error.toString());
                    }
                });
        VolleyModel.getInstance(getApplication()).addToRequestQueue(jsObjRequest);
    }

}
