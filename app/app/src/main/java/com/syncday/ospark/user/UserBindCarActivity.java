package com.syncday.ospark.user;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.Explode;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.pxy.LicensePlateView;
import com.syncday.ospark.R;
import com.syncday.ospark.common.Host;
import com.syncday.ospark.common.VolleyModel;

import org.json.JSONException;
import org.json.JSONObject;

public class UserBindCarActivity extends Activity  implements LicensePlateView.InputListener {

    private LinearLayout layout;
    private String car;
    private TextView car_tv;
    private Button button;
    //自定义键盘
    LicensePlateView keyboard_view;
    RelativeLayout keyboard_container;

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

        setContentView(R.layout.activity_bindcar);

        initView();

    }

    private void initView(){
        car_tv = findViewById(R.id.bind_car_tv);
        button = findViewById(R.id.bind_car_btn);
        keyboard_view =findViewById(R.id.bind_car_keyboard);
        keyboard_container = findViewById(R.id.bind_car_custom_keyboard_container);
        layout =findViewById(R.id.bind_car_layout);
        button = findViewById(R.id.bind_car_btn);
        keyboard_view =findViewById(R.id.bind_car_keyboard);
        keyboard_container = findViewById(R.id.bind_car_custom_keyboard_container);
        //自定义键盘相关
        //设置监听，实现对应的方法，方便在当前页面实现自己的逻辑
        keyboard_view.setInputListener(this);
        //设置父布局作为自定义键盘的容器
        keyboard_view.setKeyboardContainerLayout(keyboard_container);
        //输入七位和八位车牌号码的方法
        //mPlateView.showLastView();
        keyboard_view.hideLastView();

        car_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboard_container.setVisibility(View.VISIBLE);
            }
        });
        keyboard_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(),"无网络连接，或服务器无响应",Toast.LENGTH_LONG).show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(car!=null){
                    bindCar(car);
                }
            }
        });
    }

    private void bindCar(String car){

        Host host = new Host(getApplicationContext());
        String url = Host.DOMAIN +"/get_bind_car";
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("phone_number",host.getPhoneNumber());
            jsonObject.put("token",host.getToken());
            jsonObject.put("car",car);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // @return {   status: ,info:}
                        //status:   0,1
                        //info:
                        String status="",info="";

                        try {
                            status = response.getString("status");
                            info = response.getString("info");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplication(),info,Toast.LENGTH_SHORT).show();
                        if(status.equals("1")){
                            onBackPressed();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplication(),"无网络连接，或服务器无响应",Toast.LENGTH_LONG).show();
                    }
                });
        VolleyModel.getInstance(getApplication()).addToRequestQueue(jsObjRequest);

    }




    /**
     * 重写屏幕点击方法，点击控件外便退出活动
     * @param event
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //点击屏幕
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int[] location = new int[2];
            layout.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];
            if(event.getX() < x || event.getX() > (x + layout.getWidth()) || event.getY() < y || event.getY() > (y + layout.getHeight())){
                if(keyboard_container.getVisibility()==View.GONE){
                    onBackPressed();
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void inputComplete(String s) {
        car_tv.setText(s);
        keyboard_container.setVisibility(View.GONE);
        car = s;
    }

    @Override
    public void deleteContent() {
        car=null;
    }
}
