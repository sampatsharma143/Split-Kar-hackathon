package com.shunyank.split_kar.models;

import com.google.gson.annotations.SerializedName;

public class PhoneContact {
    @SerializedName("contact_name")
    String name;

    @SerializedName("phone_number")
    String number;

    public boolean IsFromServer =false;

    public boolean isAdded = false;

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

    public PhoneContact( String name, String number) {
        this.name = name;
        this.number = number;
    }
}
