package com.shunyank.split_kar.models;

import com.google.gson.annotations.SerializedName;

public class SettlementModel {
    @SerializedName("$id")
    String id;
    @SerializedName("$created_at")
    String createdAt;

    String group_id;
    String payer_member_name,payer_member_number,payer_member_app_id,payer_user_data;
    String receiver_member_name,receiver_member_number,receiver_member_app_id,receiver_user_data;
    boolean payer_member_is_on_app,payer_is_admin;
    boolean receiver_member_is_on_app,receiver_is_admin;
    boolean is_settled;
    String payable_amount;


    public String getPayable_amount() {
        return payable_amount;
    }

    public void setPayable_amount(String payable_amount) {
        this.payable_amount = payable_amount;
    }

    public void setPayerDetails(String payer_member_name, String payer_member_number, String payer_member_app_id, String payer_user_data, boolean payer_member_is_on_app, boolean payer_is_admin) {
        this.payer_member_name = payer_member_name;
        this.payer_member_number = payer_member_number;
        this.payer_member_app_id = payer_member_app_id;
        this.payer_user_data = payer_user_data;
        this.payer_member_is_on_app = payer_member_is_on_app;
        this.payer_is_admin = payer_is_admin;
    }

    public void setReceiverDetails(String id, String receiver_member_name, String receiver_member_number, String receiver_member_app_id, String receiver_user_data, boolean receiver_member_is_on_app, boolean receiver_is_admin) {
        this.id = id;
        this.receiver_member_name = receiver_member_name;
        this.receiver_member_number = receiver_member_number;
        this.receiver_member_app_id = receiver_member_app_id;
        this.receiver_user_data = receiver_user_data;
        this.receiver_member_is_on_app = receiver_member_is_on_app;
        this.receiver_is_admin = receiver_is_admin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getPayer_member_name() {
        return payer_member_name;
    }

    public void setPayer_member_name(String payer_member_name) {
        this.payer_member_name = payer_member_name;
    }

    public String getPayer_member_number() {
        return payer_member_number;
    }

    public void setPayer_member_number(String payer_member_number) {
        this.payer_member_number = payer_member_number;
    }

    public String getPayer_member_app_id() {
        return payer_member_app_id;
    }

    public void setPayer_member_app_id(String payer_member_app_id) {
        this.payer_member_app_id = payer_member_app_id;
    }

    public String getPayer_user_data() {
        return payer_user_data;
    }

    public void setPayer_user_data(String payer_user_data) {
        this.payer_user_data = payer_user_data;
    }

    public String getReceiver_member_name() {
        return receiver_member_name;
    }

    public void setReceiver_member_name(String receiver_member_name) {
        this.receiver_member_name = receiver_member_name;
    }

    public String getReceiver_member_number() {
        return receiver_member_number;
    }

    public void setReceiver_member_number(String receiver_member_number) {
        this.receiver_member_number = receiver_member_number;
    }

    public String getReceiver_member_app_id() {
        return receiver_member_app_id;
    }

    public void setReceiver_member_app_id(String receiver_member_app_id) {
        this.receiver_member_app_id = receiver_member_app_id;
    }

    public String getReceiver_user_data() {
        return receiver_user_data;
    }

    public void setReceiver_user_data(String receiver_user_data) {
        this.receiver_user_data = receiver_user_data;
    }

    public boolean isPayer_member_is_on_app() {
        return payer_member_is_on_app;
    }

    public void setPayer_member_is_on_app(boolean payer_member_is_on_app) {
        this.payer_member_is_on_app = payer_member_is_on_app;
    }

    public boolean isPayer_is_admin() {
        return payer_is_admin;
    }

    public void setPayer_is_admin(boolean payer_is_admin) {
        this.payer_is_admin = payer_is_admin;
    }

    public boolean isReceiver_member_is_on_app() {
        return receiver_member_is_on_app;
    }

    public void setReceiver_member_is_on_app(boolean receiver_member_is_on_app) {
        this.receiver_member_is_on_app = receiver_member_is_on_app;
    }

    public boolean isReceiver_is_admin() {
        return receiver_is_admin;
    }

    public void setReceiver_is_admin(boolean receiver_is_admin) {
        this.receiver_is_admin = receiver_is_admin;
    }

    public boolean isIs_settled() {
        return is_settled;
    }

    public void setIs_settled(boolean is_settled) {
        this.is_settled = is_settled;
    }
}
