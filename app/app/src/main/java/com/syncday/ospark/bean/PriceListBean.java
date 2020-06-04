package com.syncday.ospark.bean;

import java.util.ArrayList;
import java.util.List;

public class PriceListBean {

    private List<priceList> price_list;

    public class priceList{
        private String timeLine;
        private String price;

        public void setPrice(String price) {
            this.price = price;
        }

        public void setTimeLine(String timeLine) {
            this.timeLine = timeLine;
        }

        public String getPrice() {
            return price;
        }

        public String getTimeLine() {
            return timeLine;
        }
    }

    public List<PriceListBean.priceList> getPriceList() {
        return price_list;
    }

    public void setPriceList(List<PriceListBean.priceList> priceList) {
        this.price_list = priceList;
    }
}
