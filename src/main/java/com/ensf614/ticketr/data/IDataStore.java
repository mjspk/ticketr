package com.ensf614.ticketr.data;

import java.util.ArrayList;

import com.ensf614.ticketr.model.*;

public interface IDataStore {

    public Response<User> registerUser(User user);

    public Response<User> loginUser(String email, String password);

    public boolean logoutUser(User user);

    public Response<ArrayList<Movie>> getAllMovies();

    public Response<ArrayList<Theater>> getTheatersByMovie(Movie movie);

    public Response<ArrayList<Showtime>> getShowtimesByMovieAndTheater(Movie movie, Theater theater);

    public Response<ArrayList<Seat>> getSeatsByShowtime(Showtime showtime);

    public Response<Card> getDefaultCard(User user);

    public Response<ArrayList<Offer>> getOffers();

    public Response<User> updateUser(User user);

    public Response<User> getUser(int id);

    public ArrayList<Role> getAllRoles();

    public Response<Boolean> editRoles(User user);

    public Response<Card> getDefaultCard(int id);

    public Response<ArrayList<User>> getAllUsers();

    public Response<Boolean> deleteUser(int id);

    public Response<Boolean> deleteMovie(int id);

    public Response<Boolean> addMovie(Movie movie);

    public Response<Receipt> processPayment(Selection selection);

    public Response<ArrayList<Movie>> searchMovies(String query);

    public Response<Card> updateUserPayment(int userId, Card card);

    public boolean ifSeatsExceedsTenPercentage(int movieId, int theaterId, int showtimeId,
            String[] selectedSeatsString);

    public boolean checkSeats(int selectedShowtimeId, String[] selectedSeatsString);

    public boolean isEmailInUse(String email);
    public Response<User> addUser(User user);

}
