package com.syncday.ospark.operator;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.syncday.ospark.LauncherActivity;
import com.syncday.ospark.R;
import com.syncday.ospark.common.ChangePasswordActivity;
import com.syncday.ospark.common.Host;
import com.syncday.ospark.database.DatabaseHelper;

import java.util.Objects;

public class OperatorMyActivity extends Fragment {


    private View view;
    private TextView change_pw_tv,history_tv,logout_tv;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_operator_my,container,false);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();

        initListener();


    }

    private void initView(){
        change_pw_tv = getActivity().findViewById(R.id.operator_my_change_password);
        logout_tv = getActivity().findViewById(R.id.operator_my_logout);
        history_tv = getActivity().findViewById(R.id.operator_my_history);
    }

    private void initListener(){

        change_pw_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
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
        history_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),OperatorHistoryActivity.class);
                startActivity(intent);
            }
        });


    }

}
