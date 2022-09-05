package com.shunyank.split_kar.network.model;

import com.google.gson.annotations.SerializedName;

public class UserModel {

    @SerializedName("$id")
    String id;
    String user_name,phone_number,currency,avatar_url;
    boolean is_profile_completed;
    boolean synced;
    String upi_id = "";

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserModel(String user_name, String phone_number, String currency, String upi_id, String avatar_url, boolean is_profile_completed,
                     boolean synced) {
        this.user_name = user_name;
        this.phone_number = phone_number;
        this.currency = currency;
        this.upi_id = upi_id;
        this.avatar_url = avatar_url;
        this.is_profile_completed = is_profile_completed;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getUpi_id() {
        return upi_id;
    }

    public void setUpi_id(String upi_id) {
        this.upi_id = upi_id;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public boolean isIs_profile_completed() {
        return is_profile_completed;
    }

    public void setIs_profile_completed(boolean is_profile_completed) {
        this.is_profile_completed = is_profile_completed;
    }
}
