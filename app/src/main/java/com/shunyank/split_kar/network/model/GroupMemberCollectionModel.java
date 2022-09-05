package com.shunyank.split_kar.network.model;

import com.google.gson.annotations.SerializedName;

public class GroupMemberCollectionModel {
    @SerializedName("$id")
    String id;
    String group_id;
    String member_name,member_number,member_app_id;
    boolean member_is_on_app;
    boolean is_admin;
    String user_data;

    public String getUser_data() {
        return user_data;
    }

    public void setUser_data(String user_data) {
        this.user_data = user_data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public String getMember_number() {
        return member_number;
    }

    public void setMember_number(String member_number) {
        this.member_number = member_number;
    }

    public String getMember_app_id() {
        return member_app_id;
    }

    public void setMember_app_id(String member_app_id) {
        this.member_app_id = member_app_id;
    }

    public boolean isMember_is_on_app() {
        return member_is_on_app;
    }

    public void setMember_is_on_app(boolean member_is_on_app) {
        this.member_is_on_app = member_is_on_app;
    }

    public boolean isIs_admin() {
        return is_admin;
    }

    public void setIs_admin(boolean is_admin) {
        this.is_admin = is_admin;
    }
}
