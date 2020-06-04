package com.syncday.ospark.bean;

import java.util.List;

/**
 * 操作员获取车辆支付信息
 */
public class PayBean {
    private String car;
    private String time;
    private String address;
    private String price;

    private List<Pay> pays;

    public class Pay{
        private String platform_app;
        private String platform_url;

        public void setPlatform_app(String platform_app) {
            this.platform_app = platform_app;
        }

        public void setPlatform_url(String platform_url) {
            this.platform_url = platform_url;
        }

        public String getPlatform_app() {
            return platform_app;
        }

        public String getPlatform_url() {
            return platform_url;
        }
    }

    public void setPays(List<Pay> pays) {
        this.pays = pays;
    }

    public List<Pay> getPays() {
        return pays;
    }

    public String getTime() {
        return time;
    }

    public String getCar() {
        return car;
    }

    public String getAddress() {
        return address;
    }

    public String getPrice() {
        return price;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPrice(String price) {
        this.price = price;
    }


}
