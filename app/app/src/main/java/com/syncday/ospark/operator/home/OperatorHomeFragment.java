package com.syncday.ospark.operator.home;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.syncday.ospark.R;
import com.syncday.ospark.operator.OperatorCarDetailActivity;
import com.syncday.ospark.bean.CarBean;
import com.syncday.ospark.common.ChatActivity;
import com.syncday.ospark.operator.OperatorPayActivity;
import com.syncday.ospark.common.Host;
import com.syncday.ospark.common.VolleyModel;
import com.syncday.ospark.operator.home.recyclerview.CustomRecyclerViewAdapter;
import com.syncday.ospark.operator.home.recyclerview.OnItemClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OperatorHomeFragment extends Fragment {

    private View view;
    private RecyclerView mRecycler;
    private List<CarBean.Car> mList = new ArrayList<>();
    private CustomRecyclerViewAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_operator_home,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();

        initListener();

        getPakingAll();

    }

    /**
     * 初始化监听器
     */
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
     * 重写onResume
     */
    @Override
    public void onResume() {
        super.onResume();
        getPakingAll();
    }

    /**
     * 初始化控件
     */
    private void initView(){
        mRecycler = Objects.requireNonNull(getActivity()).findViewById(R.id.mRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(layoutManager);
        //去掉了分割线
        //mRecycler.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        if (mAdapter == null) {
            mAdapter = new CustomRecyclerViewAdapter(getActivity(), mList);
            mRecycler.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取停车信息
     */
    private void getPakingAll(){
        final Host host =new Host(Objects.requireNonNull(getActivity()));
        String phone_number = host.getPhoneNumber();
        String token = host.getToken();
        RequestQueue queue = VolleyModel.getInstance(getActivity().getApplicationContext()).
                getRequestQueue();
        //请求地址以及json格式的请求内容
        String url = Host.DOMAIN +"/get_parking_all";

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
                        // @return {   status: ,car[]: ,info:}
                        String status="",info="";
                        try {
                            status = response.getString("status");
                            info = response.getString("info");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();
                        CarBean carBean = gson.fromJson(response.toString(),CarBean.class);

                        switch (status){
                            case "-1":
                            case "0":
                                Toast.makeText(getActivity(),info,Toast.LENGTH_LONG).show();
                                break;
                            case "1":
                                String s =  carBean.getStatus();
                                mList.clear();
                                mList.addAll(carBean.getCars());
                                mAdapter.notifyDataSetChanged();
                                break;
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),"未知错误",Toast.LENGTH_LONG).show();
                        Log.d("DEBUG","get_parking_error:"+error.toString());
                    }
                });
        VolleyModel.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
    }


}
