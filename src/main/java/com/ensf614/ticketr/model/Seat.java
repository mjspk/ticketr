package com.ensf614.ticketr.model;

public class Seat {
    private int id;
    private int theaterId;
    private int row_number;
    private int seat_number;
    private String status;
    private Double price;

    public Seat(int id, int theaterId, int row_number, int seat_number, Double price, String status) {
        this.id = id;
        this.theaterId = theaterId;
        this.row_number = row_number;
        this.seat_number = seat_number;
        this.price = price;
        this.status = status;
    }
    public Seat() {
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTheaterId() {
        return theaterId;
    }

    public void setTheaterId(int theaterId) {
        this.theaterId = theaterId;
    }

   
    public int getRow_number() {
        return row_number;
    }

    public void setRow_number(int row_number) {
        this.row_number = row_number;
    }

    public int getSeat_number() {
        return seat_number;
    }

    public void setSeat_number(int seat_number) {
        this.seat_number = seat_number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
   
}
