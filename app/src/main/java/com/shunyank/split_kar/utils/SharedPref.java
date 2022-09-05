package com.shunyank.split_kar.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.shunyank.split_kar.network.model.UserModel;


public class SharedPref {

    private static String default_ringtone_url = "content://settings/system/notification_sound";
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences prefs;

    public SharedPref(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("MAIN_PREF", Context.MODE_PRIVATE);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    // Preference for User Login
    public void saveUser(String user) {
        sharedPreferences.edit().putString("USER_PREF_KEY", user).apply();
    }
    public void saveUser(UserModel userModel){
        String user = new Gson().toJson(userModel).toString();
        saveUser(user);
    }

    public static String getUserId(Context context){
        SharedPref sharedPref = new SharedPref(context);
        UserModel userModel = sharedPref.getUserModel();
        return userModel.getId();
    }
    public String getUserId(){
        UserModel userModel = getUserModel();
        return userModel.getId();
    }
    public String getMyPhoneNumber(){
        UserModel userModel = getUserModel();
        return userModel.getPhone_number();
    }
    public static String getMyPhoneNumber(Context context){
        SharedPref sharedPref = new SharedPref(context);
        UserModel userModel = sharedPref.getUserModel();
        return userModel.getPhone_number();
    }

    public static String getMyName(Context context){
        SharedPref sharedPref = new SharedPref(context);
        UserModel userModel = sharedPref.getUserModel();
        return userModel.getUser_name();
    }

    public UserModel getUserModel(){
      String userJson =  sharedPreferences.getString("USER_PREF_KEY", "");
        return new Gson().fromJson(userJson, UserModel.class);
    }
    public String getUser() {
        return sharedPreferences.getString("USER_PREF_KEY", "");
    }

    public void clearUser() {
        sharedPreferences.edit().putString("USER_PREF_KEY", null).apply();
    }

    // Preference for Fcm register
    public void setFcmRegId(String fcmRegId) {
        sharedPreferences.edit().putString("FCM_PREF_KEY", fcmRegId).apply();
    }

    public String getFcmRegId() {
        return sharedPreferences.getString("FCM_PREF_KEY", null);
    }

    public boolean isFcmRegIdEmpty() {
        return TextUtils.isEmpty(getFcmRegId());
    }

    // To save dialog permission state
    public void setNeverAskAgain(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean getNeverAskAgain(String key) {
        return sharedPreferences.getBoolean(key, false);
    }


    // Preference for first launch
    public void setFirstLaunch(boolean flag) {
        sharedPreferences.edit().putBoolean("FIRST_LAUNCH", flag).apply();
    }

    public boolean isFirstLaunch() {
        return sharedPreferences.getBoolean("FIRST_LAUNCH", true);
    }

    // Preference for settings
    public void setPushNotification(boolean value) {
        sharedPreferences.edit().putBoolean("SETTINGS_PUSH_NOTIF", value).apply();
    }

    public boolean getPushNotification() {
        return sharedPreferences.getBoolean("SETTINGS_PUSH_NOTIF", true);
    }




}
