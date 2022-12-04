package com.ensf614.ticketr.model;

public class Offer {
    private int id;
    private int userId;
    private Double price;
    private String offer;
    private int quantity;
    private Movie movie;

    public Offer() {
    }

    public Offer(int id, int userId, Double price, String offer, int quantity,Movie movie) {
        this.id = id;
        this.userId = userId;
        this.price = price;
        this.offer = offer;
        this.quantity = quantity;
        this.movie = movie;
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

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    

    

    
}
