package com.shunyank.split_kar.models;

public class PayerModel {
    String member_mobile_number,member_name;
    Float paymentPaid;

    public PayerModel(String member_mobile_number, String member_name, Float paymentPaid) {
        this.member_mobile_number = member_mobile_number;
        this.member_name = member_name;
        this.paymentPaid = paymentPaid;
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

    public Float getPaymentPaid() {
        return paymentPaid;
    }

    public void setPaymentPaid(Float paymentPaid) {
        this.paymentPaid = paymentPaid;
    }
}
