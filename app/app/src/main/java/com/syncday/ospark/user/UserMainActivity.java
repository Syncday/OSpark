package com.syncday.ospark.user;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.syncday.ospark.R;
import com.syncday.ospark.common.MessageFragment;
import com.syncday.ospark.common.Host;
import com.syncday.ospark.websocket.JWebSocketClientService;
import com.syncday.ospark.websocket.JWebsocketClient;

import java.util.ArrayList;
import java.util.List;

import static android.provider.Settings.EXTRA_APP_PACKAGE;
import static android.provider.Settings.EXTRA_CHANNEL_ID;

public class UserMainActivity extends AppCompatActivity {

    private final String TAG = "DEBUG";

    private ViewPager2 viewPager;
    private RadioGroup radioGroup;

    private JWebSocketClientService.JWebSocketClientBinder binder;
    private JWebSocketClientService jWebSClientService;
    public JWebsocketClient client;

    Host host;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        initView();

        initListener();

        initService();

        checkNotifySetting();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unbindService(connection);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initView(){
        viewPager = findViewById(R.id.user_viewPager);
        radioGroup = findViewById(R.id.user_tabs);

        ViewPagerAdpater adpater = new ViewPagerAdpater(this);
        viewPager.setAdapter(adpater);


        adpater.addFragment(new UserHomeFragment());
        adpater.addFragment(new MessageFragment());
        adpater.addFragment(new UserMyFragment());

        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(1);// 预加载,不预加载导致黑屏bug，可找苦了

    }

    /**
     * 注册监听器
     */
    private  void initListener(){
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        ((RadioButton) findViewById(R.id.user_home_tab)).setChecked(true);
                        break;
                    case 1:
                        ((RadioButton) findViewById(R.id.user_message_tab)).setChecked(true);
                        break;
                    case 2:
                        ((RadioButton) findViewById(R.id.user_my_tab)).setChecked(true);
                        break;
                }
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.user_home_tab:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.user_message_tab:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.user_my_tab:
                        viewPager.setCurrentItem(2);
                        break;
                }
            }
        });
    }

    /**
     * viewpager的适配器
     */
    public class ViewPagerAdpater extends FragmentStateAdapter {

        private List<Class> fragments;

        public ViewPagerAdpater(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
            if(fragments == null){
                fragments = new ArrayList<>();
            }
        }

        public void addFragment(Fragment fragment){
            if(fragments !=null){
                fragments.add(fragment.getClass());
            }
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            try {
                return (Fragment) fragments.get(position).newInstance();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }
    }

    /**
     * 检查通知权限
     */
    private void checkNotifySetting() {
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        boolean isOpened = manager.areNotificationsEnabled();
        if(!isOpened){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("开启通知");
            builder.setMessage("请允许App的通知权限，否则将影响正常使用");
            builder.setPositiveButton("去开启", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface ensure_dialog, int which) {
                    ensure_dialog.dismiss();
                    Intent intent = new Intent();
                    if(Build.VERSION.SDK_INT>22 && Build.VERSION.SDK_INT<25) {
                        intent.putExtra("app_package", getPackageName());
                        intent.putExtra("app_uid", getApplicationInfo().uid);
                        startActivity(intent);
                    }else if(Build.VERSION.SDK_INT>25){
                        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                        intent.putExtra(EXTRA_APP_PACKAGE, getPackageName());
                        intent.putExtra(EXTRA_CHANNEL_ID, getApplicationInfo().uid);
                        startActivity(intent);
                    }
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
    }

        //-----------------------------------------服务相关---------------------------------------------
    /**
     * 连接service
     */
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "服务与活动成功绑定");
            binder = (JWebSocketClientService.JWebSocketClientBinder) iBinder;
            jWebSClientService = binder.getService();
            client = jWebSClientService.client;
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e(TAG, "服务与活动成功断开");
        }
    };
    /**
     * 启动并绑定WebSocket服务
     */
    private void initService(){
        //启动服务
        if(!isServiceRunning("com.syncday.ospark.websocket.JWebSocketClientService",getApplication())){
            Intent intent = new Intent(getApplicationContext(), JWebSocketClientService.class);
            startService(intent);
            Log.d(TAG, "启动服务");
        }else {
            Log.d(TAG, "服务已启动，取消启动");
        }
        //绑定服务
        Intent bindIntent = new Intent(getApplicationContext(), JWebSocketClientService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE); // 绑定服务
        Log.d(TAG, "绑定服务");
    }
    /**
     * 判断服务是否正在运行
     *
     * @param serviceName 服务类的全路径名称 例如： com.jaychan.demo.service.PushService
     * @param context 上下文对象
     * @return
     */
    public static boolean isServiceRunning(String serviceName, Context context) {
        //活动管理器
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(1000); //获取运行的服务,参数表示最多返回的数量

        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            String className = runningServiceInfo.service.getClassName();
            if (className.equals(serviceName)) {
                return true; //判断服务是否运行
            }
        }
        return false;
    }
}
