package com.shunyank.split_kar.models;

import com.shunyank.split_kar.network.model.GroupMemberCollectionModel;

public class SplitAmountModel {
    GroupMemberCollectionModel memberDetails;
    String needToPay;
    String willGet;
    boolean paidByMe=false;
    String totalPaid="0";
    public boolean isPaidByMe() {
        return paidByMe;
    }

    public void setPaidByMe(boolean paidByMe) {
        this.paidByMe = paidByMe;
    }

    public SplitAmountModel(GroupMemberCollectionModel memberDetails, String needToPay, String willGet) {
        this.memberDetails = memberDetails;
        this.needToPay = needToPay;
        this.willGet = willGet;
    }

    public GroupMemberCollectionModel getMemberDetails() {
        return memberDetails;
    }

    public void setMemberDetails(GroupMemberCollectionModel memberDetails) {
        this.memberDetails = memberDetails;
    }

    public String getNeedToPay() {
        return needToPay;
    }

    public void setNeedToPay(String needToPay) {
        this.needToPay = needToPay;
    }

    public String getWillGet() {
        return willGet;
    }

    public void setWillGet(String willGet) {
        this.willGet = willGet;
    }

    public void setTotalPaid(String s) {
        totalPaid  =s;
    }

    public String getTotalPaid() {
        return totalPaid;
    }
}
