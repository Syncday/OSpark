package com.syncday.ospark.common;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.syncday.ospark.R;
import com.syncday.ospark.adapter.ChatRecyclerViewAdapter;
import com.syncday.ospark.bean.ChatBean;
import com.syncday.ospark.database.DatabaseHelper;
import com.syncday.ospark.websocket.JWebSocketClientService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private String sender,nickname;
    private Toolbar toolbar;
    private EditText editText;
    private ImageView send;
    private TextView toolbar_title;
    private RecyclerView mRecycler;
    private List<ChatBean> mList = new ArrayList<>();
    private ChatRecyclerViewAdapter mAdapter;
    private final String TAG = "DEBUG";
    MessageReceiver messageReceiver = new MessageReceiver();

    private JWebSocketClientService.JWebSocketClientBinder binder;
    private JWebSocketClientService jWebSClientService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        sender = intent.getStringExtra("sender");
        nickname =intent.getStringExtra("nickname");

        initView();

        initListener();

        initService();

        registerReceiver();

        updateMessage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }

    private void initView(){
        mRecycler = findViewById(R.id.chat_recyclerView);
        toolbar = findViewById(R.id.chat_toolbar);
        toolbar_title = findViewById(R.id.chat_toolbar_title);
        toolbar_title.setText(nickname);
        editText = findViewById(R.id.chat_editView);
        send = findViewById(R.id.chat_send);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(layoutManager);
        if (mAdapter == null) {
            mAdapter = new ChatRecyclerViewAdapter(this, mList);
            mRecycler.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void initListener(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = editText.getText().toString();
                if(msg.length()==0){
                    Toast.makeText(getApplicationContext(),"不能发空信息哦",Toast.LENGTH_SHORT).show();
                }else {
                    sendMsg(msg);
                    try {
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        View view = getWindow().peekDecorView();
                        if (null != view) {
                            Objects.requireNonNull(imm).hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

    }


    /**
     * 发送信息
     * @param msg
     */
    private void sendMsg(String msg) {
        Host host = new Host(this);
        JSONObject jsonObject =new JSONObject();
        try {
            jsonObject.put("type","message");
            jsonObject.put("from",host.getPhoneNumber());
            jsonObject.put("to",sender);
            jsonObject.put("content",msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jWebSClientService.client != null && jWebSClientService.client.isOpen()) {
            jWebSClientService.sendMsg(jsonObject.toString());
            editText.setText("");
            saveMsg(msg);
            updateMessage();
        }else {
            Toast.makeText(getApplication(),"未连接到聊天服务器，无法发送信息",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 保存信息到数据库
     */
    private void saveMsg(String msg){
        DatabaseHelper dbHelper = new DatabaseHelper(getApplication(),"ospark",null,1);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
        Date date = new Date();
        String strDateFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat,Locale.CHINA);
        String time = sdf.format(date);
        ContentValues contentValues = new ContentValues();
        contentValues.put("chat_to",sender);
        contentValues.put("chat_content",msg);
        contentValues.put("chat_nickname",nickname);
        contentValues.put("chat_time",time);
        contentValues.put("chat_read","Y");
        sqliteDatabase.insert("chat", "chat_from", contentValues);
        sqliteDatabase.close();
        dbHelper.close();
    }

    private void updateMessage(){
        DatabaseHelper dbHelper = new DatabaseHelper(this,"ospark",null,1);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();

        //设置该发送者信息已读
        sqliteDatabase.execSQL("UPDATE chat SET chat_read = 'Y' WHERE chat_from = '"+sender+"'");

        String sql = "SELECT * FROM chat WHERE chat_to = '"+sender+"' OR  chat_from = '"+sender+"' ORDER BY chat_time ASC";
        Cursor cursor = sqliteDatabase.rawQuery(sql,null);

        //利用游标遍历所有数据对象
        mList.clear();
        StringBuilder a = new StringBuilder();
        while(cursor.moveToNext()){
            ChatBean chatBean = new ChatBean();
            chatBean.setFrom(cursor.getString(cursor.getColumnIndex("chat_from")));
            chatBean.setContent(cursor.getString(cursor.getColumnIndex("chat_content")));
            chatBean.setTime(cursor.getString(cursor.getColumnIndex("chat_time")).substring(11,16));
            mList.add(chatBean);
        }
        cursor.close();
        sqliteDatabase.close();
        dbHelper.close();
        mAdapter.notifyDataSetChanged();
        mRecycler.scrollToPosition(mAdapter.getItemCount()-1);
        Log.d(TAG,"chat activity 所有信息已阅");
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
        Intent bindIntent = new Intent(ChatActivity.this, JWebSocketClientService.class);
        getApplicationContext().bindService(bindIntent, connection, BIND_AUTO_CREATE); // 绑定服务
    }

    //--------------------------------------接收广播------------------------------------------------
    /**
     * 动态注册广播
     */
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter("com.syncday.ospark.message");
        registerReceiver(messageReceiver, filter);
    }

    /**
     * 广播接收器，处理接收到的信息
     */
    private class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
           updateMessage();
        }
    }
    private void unregisterReceiver(){
        IntentFilter filter = new IntentFilter("com.syncday.ospark.message");
        unregisterReceiver(messageReceiver);
    }

}
