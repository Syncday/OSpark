package com.syncday.ospark;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.syncday.ospark.common.Host;
import com.syncday.ospark.common.VolleyModel;
import com.syncday.ospark.user.UserMainActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private final String TAG = "DEBUG";
    private EditText phone_number_editText,password_editText,password_angin_editText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //初始化界面
        phone_number_editText = findViewById(R.id.register_phone_editText);
        password_editText = findViewById(R.id.register_password_editText);
        password_angin_editText = findViewById(R.id.register_password_angin_editText);
        Button register_button = findViewById(R.id.register_button);

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone_number = phone_number_editText.getText().toString();
                String password = password_editText.getText().toString();
                String password_again = password_angin_editText.getText().toString();

                if(checkEditText(phone_number,password,password_again)){
                    getRegister(phone_number,password);
                }
            }
        });

        password_editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String password = password_editText.getText().toString();
                if(password.length()<6 || password.length()>12){
                    password_editText.setError("密码长度最短6位，最长12位");
                }
            }
        });

    }

    /**
     *
     * 检查手机号和密码长度
     * @param phone_number String
     * @param password String
     * @return Boolean
     */
    private Boolean checkEditText(String phone_number,String password,String password_again){
        Boolean flag = Boolean.TRUE;
        if(phone_number.length()!=11){
            phone_number_editText.setError("请检查手机号码");
            flag = Boolean.FALSE;
        }
        if(password.length()>12||password.length()<6){
            password_editText.setError("请检查密码长度");
            flag = Boolean.FALSE;
        }
        if(!password.equals(password_again)){
            password_angin_editText.setError("密码不相同");
            flag = Boolean.FALSE;
        }
        return flag;
    }

    /**
     * 实现注册，并显示注册状态以及页面跳转等功能
     *
     * @param password String //用户密码
     * @param phone_number  String //要注册的手机号码
     *
     * */
    private void getRegister(final String phone_number, String password){

        final Host host =new Host(this);

        RequestQueue queue = VolleyModel.getInstance(this.getApplicationContext()).
                getRequestQueue();
        //请求地址以及json格式的请求内容
        String url = Host.DOMAIN +"/register";
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("phone_number",phone_number);
            jsonObject.put("password",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // @return {   status: ,token: ,info:}
                        //status:   -0,1
                        //token：   返回的验证token，用于以后所有数据交换的验证
                        //info:
                        String status="",token="",account_type="",info="";

                        try {
                            status = response.getString("status");
                            token = response.getString("token");
                            info = response.getString("info");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        switch (status){
                            case "0":
                                Toast.makeText(getApplication(),info,Toast.LENGTH_LONG).show();
                                break;
                            case "1":
                                //持久保存手机号、账号类型以及token信息
                                host.setPhoneNumber(phone_number);
                                host.setToken(token);
                                host.setAccountType("user");

                                Toast.makeText(getApplication(),info,Toast.LENGTH_LONG).show();
                                Intent intent =new Intent(getApplication(), UserMainActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplication(),"未知错误",Toast.LENGTH_LONG).show();
                        Log.d(TAG,"register_error:"+error.toString());
                    }
                });
        VolleyModel.getInstance(this).addToRequestQueue(jsObjRequest);
    }
}
