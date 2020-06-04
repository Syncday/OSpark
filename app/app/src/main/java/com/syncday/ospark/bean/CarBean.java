package com.syncday.ospark.bean;

import java.util.List;

/**
 * 列表实体
 */
public class CarBean {
    private String status;
    private List<Car> cars;

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public List<Car> getCars() {
        return cars;
    }

    public class Car{
        private String parking_car;
        private String parking_time;
        private String parking_address;
        private Double parking_latitude;
        private Double parking_longitude;
        private String parking_user;

        public void setParking_user(String parking_user) {
            this.parking_user = parking_user;
        }

        public String getParking_user() {
            return parking_user;
        }

        public void setParking_car(String parking_car) {
            this.parking_car = parking_car;
        }

        public String getParking_car() {
            return parking_car;
        }

        public void setParking_time(String parking_time) {
            this.parking_time = parking_time;
        }

        public String getParking_time() {
            return parking_time;
        }

        public Double getParking_latitude() {
            return parking_latitude;
        }

        public Double getParking_longitude() {
            return parking_longitude;
        }

        public void setParking_address(String parking_address) {
            this.parking_address = parking_address;
        }

        public String getParking_address() {
            return parking_address;
        }

        public void setParking_latitude(Double parking_latitude) {
            this.parking_latitude = parking_latitude;
        }

        public void setParking_longitude(Double parking_longitude) {
            this.parking_longitude = parking_longitude;
        }
    }

}

