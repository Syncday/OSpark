package com.syncday.ospark.bean;

public class ChatListBean {
    private String sender;
    private String time;
    private String preview;
    private String hasNew;
    private String nickname;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTime() {
        return time;
    }

    public String getHasNew() {
        return hasNew;
    }

    public String getPreview() {
        return preview;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setHasNew(String hasNew) {
        this.hasNew = hasNew;
    }

    public void setPreview(String preView) {
        this.preview = preView;
    }
}
