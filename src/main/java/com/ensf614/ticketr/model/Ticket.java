package com.ensf614.ticketr.model;

public class Ticket {

    private int id;
    private int userId;
    private Showtime showtime;
    private Seat seat;
    private Double price;
    private String status;

    public Ticket(int id, int userId, Showtime showtime, Seat seat, Double price, String status) {
        this.id = id;
        this.userId = userId;
        this.showtime = showtime;
        this.seat = seat;
        this.price = price;
        this.status = status;
    }

    public Ticket() {
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

    public Showtime getShowtime() {
        return showtime;
    }

    public void setShowtime(Showtime showtime) {
        this.showtime = showtime;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public void setSeatId(int seatId) {
       if (seat == null) {
           seat = new Seat();
       }
         seat.setId(seatId);
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
