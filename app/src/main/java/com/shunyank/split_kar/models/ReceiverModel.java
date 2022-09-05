package com.shunyank.split_kar.models;

public class ReceiverModel {
    String member_mobile_number,member_name;
    Float paymentReceived;

    public ReceiverModel(String member_mobile_number, String member_name, Float paymentReceived) {
        this.member_mobile_number = member_mobile_number;
        this.member_name = member_name;
        this.paymentReceived = paymentReceived;
    }

    public String getMember_mobile_number() {
        return member_mobile_number;
    }

    public void setMember_mobile_number(String member_mobile_number) {
        this.member_mobile_number = member_mobile_number;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public Float getPaymentReceived() {
        return paymentReceived;
    }

    public void setPaymentReceived(Float paymentReceived) {
        this.paymentReceived = paymentReceived;
    }
}
