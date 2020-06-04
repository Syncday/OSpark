package com.syncday.ospark.websocket;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;


import com.google.gson.Gson;
import com.syncday.ospark.common.ShowNotification;
import com.syncday.ospark.database.DatabaseHelper;
import com.syncday.ospark.R;
import com.syncday.ospark.common.Host;
import com.syncday.ospark.bean.ChatBean;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Objects;

public class JWebSocketClientService extends Service {

    public JWebsocketClient client;
    private JWebSocketClientBinder mBinder = new JWebSocketClientBinder();
    private SQLiteOpenHelper dbHelper;
    private final String TAG = "DEBUG";
    private final static int GRAY_SERVICE_ID = 1001;
    //灰色保活
    public static class GrayInnerService extends Service {

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {

            startForeground(GRAY_SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
    PowerManager.WakeLock wakeLock;//锁屏唤醒
    //获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
    @SuppressLint("InvalidWakeLockTag")
    private void acquireWakeLock()
    {
        if (null == wakeLock)
        {
            PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE, "WebSocketService");
            if (null != wakeLock)
            {
                wakeLock.acquire();
            }
        }
    }

    /**
     * 发送通知
     *
     * @param message
     */
    private void sendNotification1(String title,String message) {
        if(Build.VERSION.SDK_INT<26){
            // 创建通知(标题、内容、图标)
            Notification notification = new Notification.Builder(this)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();
            // 创建通知管理器
            NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            // 发送通知
            manager.notify(1, notification);
        }else {
            // 1. 创建一个通知(必须设置channelId)
            Context context = getApplicationContext();
            String channelId = "ChannelId"; // 通知渠道
            Notification notification = new Notification.Builder(context)
                    .setChannelId(channelId)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(title)
                    .setContentText(message)
                    .build();
            // 2. 获取系统的通知管理器(必须设置channelId)
            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "消息",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            // 3. 发送通知(Notification与NotificationManager的channelId必须对应)
            notificationManager.notify(2, notification);
        }
    }


    //用于Activity和service通讯
    public class JWebSocketClientBinder extends Binder {
        public JWebSocketClientService getService() {
            return JWebSocketClientService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //初始化websocket
        initWebSocket();
        //开启心跳检测
        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);

        //设置service为前台服务，提高优先级
        if(Build.VERSION.SDK_INT>22 && Build.VERSION.SDK_INT<25){
            //Android5.1 - Android7.0，隐藏Notification上的图标
            Intent innerIntent = new Intent(this, GrayInnerService.class);
            startService(innerIntent);
            startForeground(GRAY_SERVICE_ID, new Notification());
        }else if(Build.VERSION.SDK_INT==25){
            //Android7.0app启动后通知栏会出现一条"正在运行"的通知
            startForeground(GRAY_SERVICE_ID, new Notification());
        }else if(Build.VERSION.SDK_INT>25){
            //兼容Android8以上
            Intent innerIntent = new Intent(this, GrayInnerService.class);
            startService(innerIntent);
            //startForeground(GRAY_SERVICE_ID, new Notification());
            startForegroundService(innerIntent);
        }

        acquireWakeLock();
        //内存不足时，系统保留了service的onStartCommand方法中的变量，等待系统重启此服务
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //断开WebSocket连接
        closeConnect();
        super.onDestroy();
        Log.d(TAG,"WebSocket服务停止了");
    }

    @Override
    public void onCreate() {
        //注册广播接收
        registerReceiver();
        super.onCreate();
    }

    //-------------------------------------------WebSocket------------------------------------------
    /**
     * 初始化Websocket client
     */
    private void initWebSocket(){
        URI uri = URI.create(Host.WS);
        client = new JWebsocketClient(uri){
            public void onMessage(String message) {

                Log.d(TAG,"新信息："+message);
                //处理信息
                dealWithMessage(message);
                Log.d(TAG,"处理信息完毕");

            }
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                super.onOpen(handshakedata);
                Host host =new Host(getApplication());
                String jsonString = "{\"type\":\"login\",\"phone_number\":\""+host.getPhoneNumber()+"\",\"token\":\""+host.getToken()+"\"}";
                Log.d("DEBUG",jsonString);
                client.send(jsonString);
            }
        };
        connect();
    }
    /**
     * 连接websocket
     */
    private void connect() {
        new Thread() {
            @Override
            public void run() {
                try {
                    //connectBlocking多出一个等待操作，会先连接再发送，否则未连接发送会报错
                    client.connectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    /**
     * 发送消息
     *
     * @param msg 信息内容
     */
    public void sendMsg(String msg) {
        if (null != client) {
            client.send(msg);
        }
    }
    /**
     * 断开连接
     */
    private void closeConnect()
    {
        try {
            if (null != client) {
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client = null;
        }
    }
    //----------------------------------websocket心跳检测-------------------------------------------
    private static final long HEART_BEAT_RATE = 10 * 1000;//每隔10秒进行一次对长连接的心跳检测
    private Handler mHandler = new Handler();
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            if (client != null) {
                if (client.isClosed()) {
                    reconnectWs();
                }else {
                    //发送数据，保持连接
                    try{
                        client.send("");
                    }catch (Exception exception){
                        Log.e(TAG,"服务器无响应");
                    }
                }
            } else {
                //如果client已为空，重新初始化连接
                client = null;
                initWebSocket();
            }
            //每隔一定的时间，对长连接进行一次心跳检测
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };

    /**
     * 开启重连
     */
    private void reconnectWs() {
        mHandler.removeCallbacks(heartBeatRunnable);
        new Thread() {
            @Override
            public void run() {
                try {
                    client.reconnectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    //---------------------------------------处理信息-----------------------------------------------
    /**
     * 处理信息
     * @param message String json {type,from,time,content}
     */
    private void dealWithMessage(String message){

        Gson gson = new Gson();
        ChatBean chat = gson.fromJson(message, ChatBean.class);

        switch (chat.getType()){
            case "message":
                //先保存，后广播，烦人的bug
                saveMessage(chat);
                sendBroadcast();
                break;
            case "notification":
                Log.d(TAG,"发送通知");
                String msg = chat.getContent();
                String from = chat.getFrom();
                String nickname = chat.getNickname();
                Double latitude = chat.getLatitude();
                Double longitude = chat.getLongitude();
                sendNotification(from,nickname,msg,latitude,longitude);
        }

    }
    private void saveMessage(ChatBean chat){
        // 创建 or 打开 可读/写的数据库
        DatabaseHelper dbHelper = new DatabaseHelper(getApplication(),"ospark",null,1);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("chat_from", chat.getFrom());
        values.put("chat_nickname", chat.getNickname());
        values.put("chat_content", chat.getContent());
        values.put("chat_time", chat.getTime());
        values.put("chat_read","N");
        try{
            sqliteDatabase.insert("chat", null, values);
        }catch (SQLException e){
            Log.e(TAG,"无法保存信息："+e.toString());
        }
        sqliteDatabase.close();
        dbHelper.close();
        Log.e(TAG,"保存信息"+chat.getContent());
    }

    private void sendBroadcast(){
        //发送广播
        Intent intent = new Intent();
        intent.setAction("com.syncday.ospark.message");
        sendBroadcast(intent);
    }

    /**
     * 动态注册广播
     */
    private void registerReceiver() {
        stopServiceReceiver serviceReceiver = new stopServiceReceiver();
        IntentFilter filter = new IntentFilter("com.syncday.ospark.stop.service");
        Objects.requireNonNull(getApplication()).registerReceiver(serviceReceiver, filter);
    }

    /**
     * 广播接收器,用于停止服务
     */
    private class stopServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //stopSelf();
            if (null != client) {
                client.close();
                Log.d("DEBUG","已关闭WebSocket连接");
            }
            onDestroy();
        }
    }

    //-----------------------------------------------通知-------------------------------------------
    private void sendNotification(String from,String nickname,String content,Double latitude,Double longitude){
        int notificationId =1;
        String channelId = "Notification";
        //创建通知管理器
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(getApplicationContext(), ShowNotification.class);
        intent.putExtra("info",content);
        intent.putExtra("from",from);
        intent.putExtra("nickname",nickname);
        intent.putExtra("latitude",latitude);
        intent.putExtra("longitude",longitude);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //NotificationManager.IMPORTANCE_MIN: 静默;
            //NotificationManager.IMPORTANCE_HIGH:随系统使用声音或振动
            NotificationChannel channel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            Notification notification = new Notification.Builder(getApplicationContext())
                    .setTicker("重要通知")
                    .setOngoing(true)
                    .setContentText(content)//设置通知内容
                    .setAutoCancel(true)//设置点击通知后自动删除通知
                    .setChannelId(channelId) //设置Id,安卓8.0后必须加
                    .setContentIntent(pendingIntent) //设置点击通知时的响应事件
                    .setSmallIcon(R.drawable.ospark_logo)//通知左侧的小图标
                    .build();
            manager.createNotificationChannel(channel);
            manager.notify(2, notification);
        } else {
            Notification notification = new Notification.Builder(getApplicationContext())
                    .setTicker("重要通知")
                    .setContentText(content)//设置通知内容
                    .setOngoing(true)
                    .setAutoCancel(true)//设置点击通知后自动删除通知
                    .setContentIntent(pendingIntent) //设置点击通知时的响应事件
                    .setSmallIcon(R.drawable.ospark_logo)//通知左侧的小图标
                    .setDefaults(Notification.DEFAULT_SOUND)//使用系统默认的声音或震动
                    .build();
            if (null != notification) {
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
            }
            //创建通知管理器
            manager.notify(2, notification);
        }
    }
}
