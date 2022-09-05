package com.shunyank.split_kar.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class BillModel {
    @SerializedName("$id")
    String id;
    @SerializedName("$createdAt")
    int createdAt;
    String group_id;
    String created_by;
    String event_name;
    double total_expense;
    // 0 for single person , 1 for multiperson
    int paid_type;
    int split_count=0;
    String getOrPay;
    boolean needToPay = false;
    public String getGetOrPay() {
        return getOrPay;
    }

    public boolean isNeedToPay() {
        return needToPay;
    }

    public void setNeedToPay(boolean needToPay) {
        this.needToPay = needToPay;
    }

    public void setGetOrPay(String getOrPay) {
        this.getOrPay = getOrPay;
    }

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

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public double getTotal_expense() {
        return total_expense;
    }

    public void setTotal_expense(double total_expense) {
        this.total_expense = total_expense;
    }

    public int getPaid_type() {
        return paid_type;
    }

    public void setPaid_type(int paid_type) {
        this.paid_type = paid_type;
    }

    public int getSplit_count() {
        return split_count;
    }

    public void setSplit_count(int split_count) {
        this.split_count = split_count;
    }
}
