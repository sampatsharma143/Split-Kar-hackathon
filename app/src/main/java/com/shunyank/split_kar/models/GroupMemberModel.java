package com.shunyank.split_kar.models;

import com.google.gson.annotations.SerializedName;

public class GroupMemberModel {
    String name;
    String number;

    public GroupMemberModel(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
