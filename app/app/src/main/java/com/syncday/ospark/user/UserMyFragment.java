package com.syncday.ospark.user;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.syncday.ospark.LauncherActivity;
import com.syncday.ospark.R;
import com.syncday.ospark.common.ChangePasswordActivity;
import com.syncday.ospark.database.DatabaseHelper;
import com.syncday.ospark.common.Host;
import com.syncday.ospark.common.VolleyModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class UserMyFragment extends Fragment{

    private View view;
    private TextView car_tv,bind_car_tv,unbind_car_tv,change_pw_tv,unpaid_tv,bills_tv,logout_tv;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_user_my,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();

        initListener();

    }

    @Override
    public void onResume() {
        super.onResume();
        getCar();
    }

    private void initView(){
        car_tv =getActivity().findViewById(R.id.user_my_car);
        change_pw_tv = getActivity().findViewById(R.id.user_my_change_password);
        unpaid_tv = getActivity().findViewById(R.id.user_my_unpaid);
        bills_tv = getActivity().findViewById(R.id.user_my_bills);
        logout_tv = getActivity().findViewById(R.id.user_my_logout);
        bind_car_tv = getActivity().findViewById(R.id.user_my_bind_car);
        unbind_car_tv = getActivity().findViewById(R.id.user_my_unbind_car);

    }

    private void initListener(){

        bind_car_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),UserBindCarActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });
        unbind_car_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("解除绑定当前车辆？");
                builder.setMessage("提示:\n" +
                        "\t\t正在服务时或账单未支付完成时，将无法解除绑定");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface ensure_dialog, int which) {
                        unBindCar();
                        ensure_dialog.dismiss();
                        getCar();
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

        change_pw_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });
        unpaid_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),UserUnpaidActivity.class);
                startActivity(intent);
            }
        });
        bills_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),UserPaidActivity.class);
                startActivity(intent);
            }
        });
        logout_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Host host = new Host(Objects.requireNonNull(getActivity()));
                //清除token
                host.setToken(null);
                host.setAccountType(null);
                //清除数据库
                DatabaseHelper dbHelper = new DatabaseHelper(getActivity(),"ospark",null,1);
                SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
                sqliteDatabase.execSQL("DELETE FROM chat ");
                //发送广播给服务，通知停止
                Intent broadcast = new Intent();
                broadcast.setAction("com.syncday.ospark.stop.service");
                getActivity().sendBroadcast(broadcast);
                //跳转
                Intent intent = new Intent(getActivity(), LauncherActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    private void unBindCar(){
        final Host host =new Host(Objects.requireNonNull(getActivity()));
        String phone_number = host.getPhoneNumber();
        String token = host.getToken();
        RequestQueue queue = VolleyModel.getInstance(getActivity()).
                getRequestQueue();
        //请求地址以及json格式的请求内容
        String url = Host.DOMAIN +"/get_unbind_car";
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
                        // @return { status: , info: }
                        String status ="",info="";
                        try {
                            status = response.getString("status");
                            info = response.getString("info");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getActivity(),info,Toast.LENGTH_LONG).show();

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),"无网络连接，或服务器无响应",Toast.LENGTH_LONG).show();
                    }
                });
        VolleyModel.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
    }

    private void getCar(){
        final Host host =new Host(Objects.requireNonNull(getActivity()));
        String phone_number = host.getPhoneNumber();
        String token = host.getToken();
        RequestQueue queue = VolleyModel.getInstance(getActivity()).
                getRequestQueue();
        //请求地址以及json格式的请求内容
        String url = Host.DOMAIN +"/get_user_car";
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
                        // @return { status: , car: }
                        String status ="",car="";
                        try {
                            status = response.getString("status");
                            car = response.getString("car");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        car_tv.setText(car);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),"无网络连接，或服务器无响应",Toast.LENGTH_LONG).show();
                    }
                });
        VolleyModel.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
    }


}
