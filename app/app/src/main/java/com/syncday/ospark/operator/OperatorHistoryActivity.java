package com.syncday.ospark.operator;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.syncday.ospark.R;
import com.syncday.ospark.adapter.BillRecyclerViewAdapter;
import com.syncday.ospark.bean.BillBean;
import com.syncday.ospark.common.Host;
import com.syncday.ospark.common.VolleyModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OperatorHistoryActivity extends AppCompatActivity {

    private RecyclerView mRecycler;
    private BillRecyclerViewAdapter mAdapter;
    private List<BillBean.Bill> mList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator_history);

        initView();

        getHistory();
    }

    private void initView(){
        Toolbar toolbar = findViewById(R.id.operator_history_toolbar);
        mRecycler = findViewById(R.id.operator_history_recyclerView);
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

    /**
     * 获取账单
     */
    private  void getHistory(){
        final Host host =new Host(this);
        String phone_number = host.getPhoneNumber();
        String token = host.getToken();
        RequestQueue queue = VolleyModel.getInstance(this.getApplicationContext()).
                getRequestQueue();
        //请求地址以及json格式的请求内容
        String url = Host.DOMAIN +"/get_history";
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
                        Toast.makeText(getApplication(),"无法获取历史记录",Toast.LENGTH_LONG).show();
                    }
                });
        VolleyModel.getInstance(this).addToRequestQueue(jsObjRequest);
    }
}
