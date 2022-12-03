package com.ensf614.ticketr.data;

import java.util.ArrayList;

import com.ensf614.ticketr.model.*;

public interface IDataStore {

   
    // creat all reuired methods for movie theater ticketing system
   
    // register user
    public Response< User > registerUser(User user);
    public Response < User > loginUser(String email, String password);
    // logout user
    public boolean logoutUser(User user);

    // get all movies
    public Response < ArrayList < Movie > > getAllMovies();
    // get all theaters where a movie is playing
    public Response < ArrayList < Theater > > getTheatersByMovie(Movie movie);
    // get all showtimes for specific movie in specific theater
    public Response < ArrayList < Showtime > > getShowtimesByMovieAndTheater(Movie movie, Theater theater);
    // get all seats for specific showtime
    public Response < ArrayList < Seat > > getSeatsByShowtime(Showtime showtime);

    // get user default card info
    public Response < Card > getDefaultCard(User user);
}
