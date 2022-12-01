package com.ensf614.ticketr.model;

import java.util.ArrayList;

public class Selection {

    private User user;
    private Theater selectedTheater;
    private Movie selectedMovie;
    private Showtime selectedShowtime;
    private String[] selectedSeatsString;
    private ArrayList<Ticket> selectedTickets;
    private Card card;
    public ArrayList<Theater> theaters;
    public ArrayList<Showtime> showtimes;
    public ArrayList<Seat> seats;
    private double totalPrice;

    public Selection() {
        user = new User();
        selectedMovie = new Movie();
        selectedTheater = new Theater();
        selectedShowtime = new Showtime();
        card = new Card();
        selectedSeatsString = new String[] {};
        selectedTickets = new ArrayList<Ticket>();
        theaters = new ArrayList<Theater>();
        showtimes = new ArrayList<Showtime>();
        seats = new ArrayList<Seat>();
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
        totalPrice= selectedTickets.stream().mapToDouble(Ticket::getPrice).sum();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<Seat> getSelectedSeats() {

        ArrayList<Seat> selectedSeats = new ArrayList<Seat>();
        for (String seatId : this.selectedSeatsString) {
            for (Seat seat : seats) {
                if (seat.getId() == Integer.parseInt(seatId)) {
                    selectedSeats.add(seat);
                }
            }
        }
        return selectedSeats;
       
    }

    
    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public int getSelectedTheaterId() {
        return selectedTheater.getId() ;
    }

    public void setSelectedTheaterId(int selectedTheaterId) {
        this.selectedTheater.setId(selectedTheaterId);
    }

    public int getSelectedShowtimeId() {
        return selectedShowtime.getId() ;
    }

    public void setSelectedShowtimeId(int selectedShowtimeId) {
        this.selectedShowtime.setId(selectedShowtimeId);
    }

    public Theater getSelectedTheater() {
        return selectedTheater;
    }

    public void setSelectedTheater(Theater selectedTheater) {
        this.selectedTheater = selectedTheater;
    }

    public Showtime getSelectedShowtime() {
        return selectedShowtime;
    }

    public void setSelectedShowtime(Showtime selectedShowtime) {
        this.selectedShowtime = selectedShowtime;
    }

    public void setTheaters(ArrayList<Theater> data) {

        theaters = data;
    }

    public ArrayList<Theater> getTheaters() {
        return theaters;
    }

    public void setShowtimes(ArrayList<Showtime> data) {
        showtimes = data;
    }

    public ArrayList<Showtime> getShowtimes() {
        return showtimes;
    }

    public void setSeats(ArrayList<Seat> data) {
        seats = data;
    }

    public ArrayList<Seat> getSeats() {
        return seats;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String[] getSelectedSeatsString() {
        return selectedSeatsString;
    }

    public void setSelectedSeatsString(String[] selectedSeatsString) {
        this.selectedSeatsString = selectedSeatsString;
    }

}
