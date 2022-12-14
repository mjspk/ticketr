package com.ensf614.ticketr.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ensf614.ticketr.model.*;

public interface IDataStore {

    public Response<User> registerUser(User user);

    public Response<User> loginUser(String email, String password);

    public boolean logoutUser(User user);

    public Response<ArrayList<Movie>> getAllMovies();

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

    public Response<Movie> addMovie(Movie movie);

    public Response<Receipt> processPayment(Selection selection);

    public Response<ArrayList<Movie>> searchMovies(String query);

    public Response<Card> updateUserPayment(int userId, Card card);

    public boolean ifSeatsExceedsTenPercentage(int movieId, int theaterId, int showtimeId,
            String[] selectedSeatsString);

    public boolean checkSeats(int selectedShowtimeId, String[] selectedSeatsString);

    public boolean isEmailInUse(String email);

    public Response<User> addUser(User user);

    Response<List<Ticket>> getTicketsByUserEmail(String name);

    public Response<ArrayList<Ticket>> addTickets(ArrayList<Ticket> tickets, int userId);

    public Response<ArrayList<News>> getMoviesNews();

    public Response<Seat> getSeat(int seatId);

    public Response<ArrayList<Seat>> getSeats(int movieId, int showtimeId);

    public Response<Showtime> getShowtime(int showtimeId);

    public Response<ArrayList<Theater>> getAllTheaters(int movieId);

    public Response<ArrayList<Showtime>> getShowtimes(int movieId, int theaterId);

    public Response<Movie> getMovie(int movieId);

    public Response<ArrayList<Movie>> getUpcomingMovies();

    public Response<ArrayList<Movie>> getAllShowingMovies();

    public Response<Boolean> deleteTicket(int id);

    public Response<Boolean> cancelTicket(Ticket id);

    public Response<Ticket> getTicket(int id);

    public Response<User> getUserByEmail(String email);

    public Response<Boolean> addUserCredit(int userId, double amount);
    public Response<List<String>> getAllUsersEmails();

}
