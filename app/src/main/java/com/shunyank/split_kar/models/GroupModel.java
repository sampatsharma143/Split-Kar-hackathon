package com.shunyank.split_kar.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class GroupModel {
    @SerializedName("$id")
    String id;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @SerializedName("group_name")
    String groupName;

    String admin;
    String members;
    @SerializedName("on_app_users")
    String onAppUsers;
    @SerializedName("$createdAt")
    int createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    public GroupModel(String admin, String members, String onAppUsers) {
        this.admin = admin;
        this.members = members;
        this.onAppUsers = onAppUsers;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public List<GroupMemberModel> getMembers() {
        return new Gson().fromJson(members, new TypeToken<ArrayList<GroupMemberModel>>() {}.getType());
    }

    public String getMembersAsString(){
        return members;
    }
    public void setMembers(String members) {
        this.members = members;
    }

    public void setOnAppUsers(String onAppUsers) {
        this.onAppUsers = onAppUsers;
    }
}
