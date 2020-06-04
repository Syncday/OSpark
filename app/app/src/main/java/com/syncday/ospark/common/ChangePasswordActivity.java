package com.syncday.ospark.common;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.Explode;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.syncday.ospark.R;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordActivity extends Activity {

    private LinearLayout layout;
    private EditText password_new,password_re;
    private Button button;

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

        setContentView(R.layout.activity_change_password);

        password_new = findViewById(R.id.change_password_new);
        password_re = findViewById(R.id.change_password_re);

        layout =findViewById(R.id.change_password_layout);
        button = findViewById(R.id.change_password_btn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = password_new.getText().toString();
                String password_again = password_re.getText().toString();

                if(checkEditText(password,password_again)){
                    changePassword(password);
                }
            }
        });
        password_new.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String password = password_new.getText().toString();
                if(password.length()<6 || password.length()>12){
                    password_new.setError("密码长度最短6位，最长12位");
                }
            }
        });


    }

    /**
     *
     * 检查密码
     * @param password String
     * @return Boolean
     */
    private Boolean checkEditText(String password,String password_again){
        Boolean flag = Boolean.TRUE;
        if(password.length()>12||password.length()<6){
            password_new.setError("请检查密码长度");
            flag = Boolean.FALSE;
        }
        if(!password.equals(password_again)){
            password_re.setError("密码不相同");
            flag = Boolean.FALSE;
        }
        return flag;
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
                onBackPressed();
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void changePassword(String password){
        final Host host =new Host(this);
        String phone_number = host.getPhoneNumber();
        String token = host.getToken();
        RequestQueue queue = VolleyModel.getInstance(this.getApplicationContext()).
                getRequestQueue();
        //请求地址以及json格式的请求内容
        String url = Host.DOMAIN +"/get_change_password";
        final JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("phone_number",phone_number);
            jsonObject.put("token",token);
            jsonObject.put("password",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // @return { status: , info: }
                        String status = "",info="";
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
                        Toast.makeText(getApplication(),"无法连接到服务器",Toast.LENGTH_LONG).show();
                    }
                });
        VolleyModel.getInstance(this).addToRequestQueue(jsObjRequest);
    }
}
