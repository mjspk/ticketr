package com.ensf614.ticketr.model;

import java.util.ArrayList;

public class Theater {
    private int id;
    private String name;
    private String address;
    private String city;
    private String province;
    private String postalCode;
    private String phone;
    private String email;
    private String website;

    ArrayList<Showtime> showTimes = new ArrayList<Showtime>();

    public Theater(int id, String name, String address, String city, String province, String postalCode, String phone, String email, String website) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.province = province;
        this.postalCode = postalCode;
        this.phone = phone;
        this.email = email;
        this.website = website;
    }

    public Theater() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setShowtimes(ArrayList<Showtime> showtimes2) {
        this.showTimes = showtimes2;
    }

    public ArrayList<Showtime> getShowTimes() {
        return showTimes;
    }

    
}
