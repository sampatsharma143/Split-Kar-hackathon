package com.shunyank.split_kar.models;

public class FriendAndContactModel {
    String name;
    String contact;
    String id;
    String avatarUrl;
    boolean onServerFriendList;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public FriendAndContactModel(String id, String name, String contact, boolean onServerFriendList) {
        this.name = name;
        this.contact = contact;
        this.id = id;
        this.onServerFriendList = onServerFriendList;
    }
    public FriendAndContactModel(String name, String contact, boolean onServerFriendList) {
        this.name = name;
        this.contact = contact;
        this.onServerFriendList = onServerFriendList;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isOnServerFriendList() {
        return onServerFriendList;
    }

    public void setOnServerFriendList(boolean onServerFriendList) {
        this.onServerFriendList = onServerFriendList;
    }
}
