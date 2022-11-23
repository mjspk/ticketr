package com.ensf614.ticketr.model;

import java.util.ArrayList;

public class Selection {

    private User user;
    private int selectedTheaterId;
    private Movie selectedMovie;
    private int selectedShowtimeId;
    private String[] selectedSeats;
    private ArrayList<Ticket> selectedTickets;
    private Card card;

    public Selection() {
        user = new User();
        selectedMovie = new Movie();
        card=new Card();
        selectedSeats = new String[] {};
        selectedTickets = new ArrayList<Ticket>();
    }

    

    public Movie getSelectedMovie() {
        return selectedMovie;
    }

    public void setSelectedMovie(Movie selectedMovie) {
        this.selectedMovie = selectedMovie;
    }

    public ArrayList<Ticket> getSelectedTickets() {
        return selectedTickets;
    }

    public void setSelectedTickets(ArrayList<Ticket> selectedTickets) {
        this.selectedTickets = selectedTickets;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String[] getSelectedSeats() {
        return selectedSeats;
    }

    public void setSelectedSeats(String[] selectedSeats) {
        this.selectedSeats = selectedSeats;
    }
    
    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public int getSelectedTheaterId() {
        return selectedTheaterId;
    }

    public void setSelectedTheaterId(int selectedTheaterId) {
        this.selectedTheaterId = selectedTheaterId;
    }

    public int getSelectedShowtimeId() {
        return selectedShowtimeId;
    }

    public void setSelectedShowtimeId(int selectedShowtimeId) {
        this.selectedShowtimeId = selectedShowtimeId;
    }
}
