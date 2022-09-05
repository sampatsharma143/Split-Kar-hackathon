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
}
