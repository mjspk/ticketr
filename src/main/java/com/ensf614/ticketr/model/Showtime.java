package com.ensf614.ticketr.model;

import java.util.ArrayList;

public class Showtime {
    private int id;
    private String date;
    private String time;
    private Theater theater;
    private Movie movie;
    String[] selectedSeats;

    

    public Showtime(int id, String date, String time, Theater theater, Movie movie) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.theater = theater;
        this.movie = movie;
        selectedSeats = new String[] {};
    }

    public Showtime() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Theater getTheater() {
        return theater;
    } 

    public void setTheater(Theater theater) {
        this.theater = theater;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public String[] getSelectedSeats() {
        return selectedSeats;
    }

    public void setSelectedSeats(String[] selectedSeats) {
        this.selectedSeats = selectedSeats;
    }
}
