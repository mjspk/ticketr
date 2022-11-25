package com.ensf614.ticketr.model;

public class Offers {
    private int id;
    private int userId;
    private Double price;
    private String offer;
    private int quantity;

    public Offers() {
    }

    public Offers(int id, int userId, Double price, String offer, int quantity) {
        this.id = id;
        this.userId = userId;
        this.price = price;
        this.offer = offer;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    

    

    
}
