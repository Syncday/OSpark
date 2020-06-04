package com.syncday.ospark.bean;

public class ChatBean {
    private String from;
    private String time;
    private String content;
    private String nickname;
    private String read;
    private String type;
    private Double latitude;
    private Double longitude;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public String getRead() {
        return read;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }

    public String getFrom() {
        return from;
    }
}
