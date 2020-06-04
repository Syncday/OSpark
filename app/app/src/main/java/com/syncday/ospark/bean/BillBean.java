package com.syncday.ospark.bean;

import java.util.List;

public class BillBean {
    private String count;
    private List<Bill> bills;

    public List<Bill> getBills() {
        return bills;
    }

    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public class Bill{
        private String bill_id;
        private String bill_car;
        private String bill_create_time;
        private String bill_finish_time;
        private String bill_address;
        private String bill_price;

        public void setBill_id(String bill_id) {
            this.bill_id = bill_id;
        }

        public void setBill_address(String bill_address) {
            this.bill_address = bill_address;
        }

        public void setBill_car(String bill_car) {
            this.bill_car = bill_car;
        }

        public void setBill_create_time(String bill_create_time) {
            this.bill_create_time = bill_create_time;
        }

        public void setBill_finish_time(String bill_finish_time) {
            this.bill_finish_time = bill_finish_time;
        }

        public void setBill_price(String bill_price) {
            this.bill_price = bill_price;
        }

        public String getBill_address() {
            return bill_address;
        }

        public String getBill_car() {
            return bill_car;
        }

        public String getBill_create_time() {
            return bill_create_time;
        }

        public String getBill_finish_time() {
            return bill_finish_time;
        }

        public String getBill_id() {
            return bill_id;
        }

        public String getBill_price() {
            return bill_price;
        }
    }
}
