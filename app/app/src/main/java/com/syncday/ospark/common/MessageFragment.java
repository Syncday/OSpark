package com.syncday.ospark.common;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.syncday.ospark.R;
import com.syncday.ospark.adapter.OnItemEventListener;
import com.syncday.ospark.database.DatabaseHelper;
import com.syncday.ospark.bean.ChatListBean;
import com.syncday.ospark.adapter.MessageRecyclerViewAdapter;
import com.syncday.ospark.websocket.JWebSocketClientService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.BIND_AUTO_CREATE;

public class MessageFragment extends Fragment {

    private View view;
    private JWebSocketClientService.JWebSocketClientBinder binder;
    private JWebSocketClientService jWebSClientService;
    private RecyclerView mRecycler;
    private boolean isConnected = false;
    private MessageRecyclerViewAdapter mAdapter;
    private List<ChatListBean> mList = new ArrayList<>();
    private static String TAG = "DEBUG";

    private MessageReceiver messageReceiver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_message,container,false);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initService();

        registerReceiver();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();

    }

    @Override
    public void onResume() {
        new getData().execute();
        super.onResume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
           Objects.requireNonNull(getActivity()).getApplicationContext().unbindService(connection);
           getActivity().unregisterReceiver(messageReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initView(){
        mRecycler = Objects.requireNonNull(getActivity()).findViewById(R.id.chat_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(layoutManager);
        if (mAdapter == null) {
            mAdapter = new MessageRecyclerViewAdapter(getActivity(), mList);
            mRecycler.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
        //事件监听,处理相关事件
        mAdapter.setOnItemEventListener(new OnItemEventListener<ChatListBean>() {

            //跳转新活动
            @Override
            public void onItemClick(ChatListBean bean, View view, int position) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("sender",bean.getSender());
                intent.putExtra("nickname",bean.getNickname());
                startActivity(intent);
            }
            //删除信息
            @Override
            public void onItemLongClick(final ChatListBean bean, View view, int position) {
                //布局文件转换为View对象
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_delete, null);
                // 新建对话框对象
                final Dialog dialog = new AlertDialog.Builder(getActivity()).create();
                dialog.setCancelable(false);
                dialog.show();
                dialog.getWindow().setContentView(layout);
                //
                Button cancel = layout.findViewById(R.id.dialog_button_cancel);
                Button delete = layout.findViewById(R.id.dialog_button_delete);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteItem(bean.getSender());
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    /**
     * 删除列表item
     */
    private void deleteItem(String sender){
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity(),"ospark",null,1);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
        try {
            sqliteDatabase.execSQL("DELETE FROM chat WHERE chat_from = '"+sender+"' OR chat_to = '"+sender+"'");
        }catch (SQLException e){
           Toast.makeText(getActivity(),"删除失败",Toast.LENGTH_SHORT).show();
        }
        sqliteDatabase.close();
        dbHelper.close();
        new getData().execute();
    }

    //-----------------------------------------服务相关---------------------------------------------
    /**
     * 连接service
     */
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = (JWebSocketClientService.JWebSocketClientBinder) iBinder;
            jWebSClientService = binder.getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    /**
     * 绑定WebSocket服务
     */
    private void initService(){
        //不能再次启动服务，否则会产生两个进程
        Intent bindIntent = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), JWebSocketClientService.class);
        isConnected = getActivity().getApplicationContext().bindService(bindIntent, connection, BIND_AUTO_CREATE); // 绑定服务
        Log.e(TAG, "绑定服务");
    }

    //--------------------------------------接收广播------------------------------------------------
    /**
     * 动态注册广播
     */
    private void registerReceiver() {
        messageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter("com.syncday.ospark.message");
        Objects.requireNonNull(getActivity()).registerReceiver(messageReceiver, filter);
    }

    /**
     * 广播接收器，处理接收到的信息
     */
    private class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            new getData().execute();
        }
    }

    //------------------------------------子线程获取数据--------------------------------------------

    @SuppressLint("StaticFieldLeak")
    class getData extends AsyncTask<Void, Integer, Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            updateChatList();
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mAdapter.notifyDataSetChanged();
        }
    }
    /**
     * 获取数据
     */
    private void updateChatList(){
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity(),"ospark",null,1);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();

        //不明白，为什么括号内的ASC效果会相反？
        String sql = "SELECT * FROM " +
                "(SELECT * FROM chat WHERE chat_from NOT NULL OR chat_to NOT IN(SELECT chat_from FROM chat WHERE chat_from NOT NULL)" +
                "ORDER BY chat_time ASC )t GROUP BY t.chat_from ORDER BY t.chat_time DESC";
        Cursor cursor = sqliteDatabase.rawQuery(sql,null);

        //利用游标遍历所有数据对象
        mList.clear();
        //StringBuilder s = new StringBuilder();
        while(cursor.moveToNext()){
            ChatListBean chatListBean = new ChatListBean();
            //自己发起的信息，对方还没有回信息
            if(cursor.getString(cursor.getColumnIndex("chat_from"))==null||cursor.getString(cursor.getColumnIndex("chat_from")).equals("")){
                chatListBean.setSender(cursor.getString(cursor.getColumnIndex("chat_to")));
                chatListBean.setPreview("");
            }else{//正常处理
                chatListBean.setSender(cursor.getString(cursor.getColumnIndex("chat_from")));
                chatListBean.setPreview(cursor.getString(cursor.getColumnIndex("chat_content")));
            }

            chatListBean.setNickname(cursor.getString(cursor.getColumnIndex("chat_nickname")));
            chatListBean.setTime(cursor.getString(cursor.getColumnIndex("chat_time")));
            //是否有新信息
            if(cursor.getString(cursor.getColumnIndex("chat_read")).equals("N")){
                chatListBean.setHasNew("New");
            }else {
                chatListBean.setHasNew("");
            }
            mList.add(chatListBean);
//
//            s.append("chat_from:").append(cursor.getString(cursor.getColumnIndex("chat_from"))).append("\n")
//                    .append("chat_to:").append(cursor.getString(cursor.getColumnIndex("chat_to"))).append("\n")
//                    .append("chat_content:").append(cursor.getString(cursor.getColumnIndex("chat_content"))).append("\n")
//                    .append("chat_time:").append(cursor.getString(cursor.getColumnIndex("chat_time"))).append("\n")
//                    .append("chat_read:").append(cursor.getString(cursor.getColumnIndex("chat_read"))).append("\n\n");
        }
        //Log.d("DEBUG",s.toString());
        cursor.close();
        sqliteDatabase.close();
        dbHelper.close();
    }
}
