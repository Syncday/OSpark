package com.syncday.ospark;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.syncday.ospark.common.Host;
import com.syncday.ospark.common.VolleyModel;
import com.syncday.ospark.operator.OperatorMainActivity;
import com.syncday.ospark.user.UserMainActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class LauncherActivity extends AppCompatActivity {

    private Host host;

    private final String TAG = "DEBUG";
    private EditText phone_editText,password_editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        host = new Host(this);

        if(host.getAccountType()!=null){
            String account = host.getAccountType();
            switch (account){
                case "user":
                    Intent intent =new Intent(getApplication(), UserMainActivity.class);
                    startActivity(intent);
                    break;
                case "operator":
                    Intent intent2 =new Intent(getApplication(), OperatorMainActivity.class);
                    startActivity(intent2);
                    break;
            }
            this.finish();
        }
        //初始化界面
        setContentView(R.layout.activity_launcher);

        Log.d(TAG,"oncreate");
        Button login_button = findViewById(R.id.login_button);
        phone_editText = findViewById(R.id.phone_editText);
        password_editText = findViewById(R.id.password_editText);
        Button register_button = findViewById(R.id.register_button);
        if(host.getPhoneNumber()!=null){
            phone_editText.setText(host.getPhoneNumber());
        }
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone_number = phone_editText.getText().toString();
                String password = password_editText.getText().toString();

                if (checkEditText(phone_number,password)){
                    getLogin(phone_number,password);
                }
            }
        });
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplication(), RegisterActivity.class);
                startActivity(intent);
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
    private Boolean checkEditText(String phone_number,String password){
        Boolean flag = Boolean.TRUE;
        if(phone_number.length()!=11){
            phone_editText.setError("请检查手机号码");
            flag = Boolean.FALSE;
        }
        if(password.length()>12||password.length()<6){
            password_editText.setError("请检查密码");
            flag = Boolean.FALSE;
        }
        return flag;
    }

    /**
     * 实现登录，并显示登录状态以及页面跳转等功能
     *
     * @param password String //用户密码
     * @param phone_number  String //手机号码
     *
     * */
    private void getLogin(final String phone_number, String password){

        RequestQueue queue = VolleyModel.getInstance(this.getApplicationContext()).
                getRequestQueue();
        //请求地址以及json格式的请求内容
        String url = Host.DOMAIN +"/login";
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
                        //@return   { status: ,token:    ,account_type:   ,info}
                        //status:   -0,1
                        //token：   返回的验证token，用于以后所有数据交换的验证
                        //account_type： user，operator
                        //info:
                        String status="",token="",account_type="",info = "";

                        try {
                            status = response.getString("status");
                            token = response.getString("token");
                            account_type = response.getString("account_type");
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
                                host.setAccountType(account_type);

                                Toast.makeText(getApplication(),info,Toast.LENGTH_LONG).show();
                                //根据用户类型进入相应的主页面
                                if(account_type.equals("operator")){
                                    Intent intent =new Intent(getApplication(), OperatorMainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else if(account_type.equals("user")){
                                    Intent intent =new Intent(getApplication(), UserMainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                break;
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplication(),"未知错误",Toast.LENGTH_LONG).show();
                        Log.d(TAG,"login_error:"+error.toString());
                    }
                });
        VolleyModel.getInstance(this).addToRequestQueue(jsObjRequest);
    }

}
