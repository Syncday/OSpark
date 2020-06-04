package com.syncday.ospark.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 自定义类，可以获取保存着app的请求api地址，以及保存在本地的账号账号信息
 */
public class Host {
    //接口地址
    public static final String DOMAIN = "https://syncday.com/api";
    public static final String WS = "ws://23.95.231.104:8282";
    private SharedPreferences sharedPreferences;

    public Host(Context context){
        sharedPreferences = context.getSharedPreferences("HOST", Context.MODE_PRIVATE);
    }

    public void setPhoneNumber(String phoneNumber){
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString("phoneNumber",phoneNumber).commit();
    }
    public void setToken(String token){
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString("token",token).commit();
    }
    public void setAccountType(String accountType){
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString("accountType",accountType).commit();
    }
    public String getPhoneNumber(){
        return sharedPreferences.getString("phoneNumber", null);
    }
    public String getToken(){
        return sharedPreferences.getString("token", null);
    }
    public String getAccountType(){
        return sharedPreferences.getString("accountType", null);
    }

}
