package com.ensf614.ticketr.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ensf614.ticketr.model.Movie;
import com.ensf614.ticketr.model.Response;
import com.ensf614.ticketr.model.Seat;
import com.ensf614.ticketr.model.Showtime;
import com.ensf614.ticketr.model.Theater;
import com.ensf614.ticketr.model.Ticket;
import com.ensf614.ticketr.model.User;

@Component("dataStore")
public class DataStore implements IDataStore {
    @Autowired
    private Environment env;

    private Statement getStatement() throws SQLException, ClassNotFoundException {
        Connection conn = DriverManager.getConnection(env.getProperty("db-url"), env.getProperty("db-username"),
                env.getProperty("db-password"));
        Statement stmt = conn.createStatement();
        return stmt;
    }

    private boolean checkSQLInjection(String str) {
        String regex = "((\\%27)|(\\'))((\\%6F)|o|(\\%4F))((\\%72)|r|(\\%52))";
        return str.matches(regex);

    }

    @Override
    public Response<User> registerUser(User user) {
        Response<User> response = new Response<User>();
        try {
            if (checkSQLInjection(user.getFirstName()) || checkSQLInjection(user.getLastName())
                    || checkSQLInjection(user.getEmail()) || checkSQLInjection(user.getPassword())) {
                response.setSuccess(false);
                response.setMessage("SQL Injection detected");
                return response;
            }
            if (checkEmailExists(user.getEmail())) {
                response.setSuccess(false);
                response.setMessage("Email already exists");
                return response;
            }
            if (user.getPassword() == null || user.getPassword().length() < 8) {
                response.setSuccess(false);
                response.setMessage("Password must be at least 8 characters");
                return response;
            }
            Statement stmt = getStatement();
            String sql = "INSERT INTO user (first_name, last_name, email, address, city, province, postal_code, phone, password) VALUES ('"
                    + user.getFirstName() + "', '" + user.getLastName() + "', '" + user.getEmail() + "', '"
                    + user.getAddress() + "', '" + user.getCity() + "', '" + user.getProvince() + "', '"
                    + user.getPostalCode() + "', '" + user.getPhone() + "', '" + user.getPassword() + "')";
            stmt.executeUpdate(sql);
            response.setSuccess(true);
            response.setMessage("User registered successfully");
            response.setData(user);
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error registering user");
            return response;
        }

    }

    public Response<User> addUser(User user) {
        Response<User> response = new Response<User>();
        try {
            if (checkSQLInjection(user.getFirstName()) || checkSQLInjection(user.getLastName())
                    || checkSQLInjection(user.getEmail())) {
                response.setSuccess(false);
                response.setMessage("SQL Injection detected");
                return response;
            }
            if (checkEmailExists(user.getEmail())) {
                response.setSuccess(false);
                response.setMessage("Email already exists");
                return response;
            }

            Statement stmt = getStatement();
            String sql = "INSERT INTO user (first_name, last_name, email, address, city, province, postal_code, phone, password) VALUES ('"
                    + user.getFirstName() + "', '" + user.getLastName() + "', '" + user.getEmail() + "', '"
                    + user.getAddress() + "', '" + user.getCity() + "', '" + user.getProvince() + "', '"
                    + user.getPostalCode() + "', '" + user.getPhone() + "', 'None')";
            stmt.executeUpdate(sql);
            response.setSuccess(true);
            response.setMessage("User registered successfully");
            response.setData(user);
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error registering user");
            return response;
        }

    }

    private boolean checkEmailExists(String email) {
        try {
            Statement stmt = getStatement();
            String sql = "SELECT * FROM user WHERE email = '" + email + "'";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Response<User> loginUser(String email, String password) {
        Response<User> response = new Response<User>();
        try {
            if (checkSQLInjection(email) || checkSQLInjection(password)) {
                response.setSuccess(false);
                response.setMessage("SQL Injection detected");
                return response;
            }
            Statement stmt = getStatement();
            String sql = "SELECT * FROM user WHERE email = '" + email + "' AND password = '"
                    + password + "'";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                response.setSuccess(true);
                response.setMessage("User logged in successfully");
                response.setData(user);

                return response;
            } else {
                response.setSuccess(false);
                response.setMessage("Invalid email or password");
                return response;
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error logging in user");
            return response;
        }
    }

    @Override
    public boolean logoutUser(User user) {
        return false;
    }

    @Override
    public Response<ArrayList<Movie>> getAllMovies() {
        Response<ArrayList<Movie>> response = new Response<ArrayList<Movie>>();
        try {
            Statement stmt = getStatement();
            String sql = "SELECT * FROM movie";
            ResultSet rs = stmt.executeQuery(sql);
            ArrayList<Movie> movies = new ArrayList<Movie>();
            while (rs.next()) {
                Movie movie = new Movie();
                movie.setId(rs.getInt("id"));
                movie.setTitle(rs.getString("title"));
                movie.setGenre(rs.getString("genre"));
                movie.setRating(rs.getString("rating"));
                movie.setDuration(rs.getString("duration"));
                movie.setReleaseDate(rs.getString("release_date"));
                movie.setPoster(rs.getString("poster"));
                movies.add(movie);
            }
            response.setSuccess(true);
            response.setMessage("Movies retrieved successfully");
            response.setData(movies);
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error retrieving movies");
            return response;
        }
    }

    @Override
    public Response<ArrayList<Theater>> getTheatersByMovie(Movie movie) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Response<ArrayList<Showtime>> getShowtimesByMovieAndTheater(Movie movie, Theater theater) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Response<ArrayList<Seat>> getSeatsByShowtime(Showtime showtime) {
        // TODO Auto-generated method stub
        return null;
    }

    public User getUserByEmail(String email) {
        try {
            Statement stmt = getStatement();
            String sql = "SELECT * FROM user WHERE email = '" + email + "'";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                return user;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public Response<Movie> getMovie(int movieId) {

        Response<Movie> response = new Response<Movie>();
        try {
            Statement stmt = getStatement();
            String sql = "SELECT * FROM movie WHERE id = " + movieId;
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                Movie movie = new Movie();
                movie.setId(rs.getInt("id"));
                movie.setTitle(rs.getString("title"));
                movie.setGenre(rs.getString("genre"));
                movie.setRating(rs.getString("rating"));
                movie.setDuration(rs.getString("duration"));
                movie.setReleaseDate(rs.getString("release_date"));
                movie.setPoster(rs.getString("poster"));
                response.setSuccess(true);
                response.setMessage("Movie retrieved successfully");
                response.setData(movie);
                return response;
            } else {
                response.setSuccess(false);
                response.setMessage("Movie not found");
                return response;
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error retrieving movie");
            return response;
        }
    }

    public Response<ArrayList<Showtime>> getShowtimes(int movieId, int theaterId) {

        Response<ArrayList<Showtime>> response = new Response<ArrayList<Showtime>>();
        try {
            Statement stmt = getStatement();
            String sql = "SELECT * FROM showtime WHERE movie_id = " + movieId + " AND theater_id = " + theaterId;
            ResultSet rs = stmt.executeQuery(sql);
            ArrayList<Showtime> showtimes = new ArrayList<Showtime>();
            while (rs.next()) {
                Showtime showtime = new Showtime();
                showtime.setId(rs.getInt("id"));
                showtime.setTime(rs.getString("time"));
                showtime.setDate(rs.getString("date"));
                showtimes.add(showtime);
            }
            response.setSuccess(true);
            response.setMessage("Showtimes retrieved successfully");
            response.setData(showtimes);
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error retrieving showtimes");
            return response;
        }
    }

    public Response<ArrayList<Theater>> getAllTheaters(int movieId) {

        Response<ArrayList<Theater>> response = new Response<ArrayList<Theater>>();
        try {
            Statement stmt = getStatement();
            String sql = "SELECT * FROM theater";
            ResultSet rs = stmt.executeQuery(sql);
            ArrayList<Theater> theaters = new ArrayList<Theater>();
            while (rs.next()) {
                Theater theater = new Theater();
                theater.setId(rs.getInt("id"));
                theater.setName(rs.getString("name"));
                theaters.add(theater);
            }
            response.setSuccess(true);
            response.setMessage("Theaters retrieved successfully");
            response.setData(theaters);
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error retrieving theaters");
            return response;
        }
    }

    public Response<Showtime> getShowtime(int showtimeId) {

        Response<Showtime> response = new Response<Showtime>();
        try {
            Statement stmt = getStatement();
            String sql = "SELECT * FROM showtime WHERE id = " + showtimeId;
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                Showtime showtime = new Showtime();
                showtime.setId(rs.getInt("id"));
                showtime.setTime(rs.getString("time"));
                showtime.setDate(rs.getString("date"));
                response.setSuccess(true);
                response.setMessage("Showtime retrieved successfully");
                response.setData(showtime);
                return response;
            } else {
                response.setSuccess(false);
                response.setMessage("Showtime not found");
                return response;
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error retrieving showtime");
            return response;
        }
    }

    public Response<ArrayList<Seat>> getSeats(int showtimeId) {

        Response<ArrayList<Seat>> response = new Response<ArrayList<Seat>>();
        try {
            Statement stmt = getStatement();
            String sql = "SELECT * FROM seat join showtime on seat.theater_id = showtime.theater_id WHERE showtime.id = "
                    + showtimeId;
            ResultSet rs = stmt.executeQuery(sql);
            ArrayList<Seat> seats = new ArrayList<Seat>();
            while (rs.next()) {
                Seat seat = new Seat();
                seat.setId(rs.getInt("id"));
                seat.setRow_number(rs.getInt("row_number"));
                seat.setSeat_number(rs.getInt("seat_number"));
                seat.setPrice(rs.getDouble("price"));
                seat.setStatus(rs.getString("status"));
                seats.add(seat);
            }
            response.setSuccess(true);
            response.setMessage("Seats retrieved successfully");
            response.setData(seats);
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error retrieving seats");
            return response;
        }
    }

    public Response<Seat> getSeat(int seatId) {

        Response<Seat> response = new Response<Seat>();
        try {
            Statement stmt = getStatement();
            String sql = "SELECT * FROM seat WHERE id = " + seatId;
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                Seat seat = new Seat();
                seat.setId(rs.getInt("id"));
                seat.setRow_number(rs.getInt("row_number"));
                seat.setSeat_number(rs.getInt("seat_number"));
                seat.setPrice(rs.getDouble("price"));
                seat.setStatus(rs.getString("status"));
                response.setSuccess(true);
                response.setMessage("Seat retrieved successfully");
                response.setData(seat);
                return response;
            } else {
                response.setSuccess(false);
                response.setMessage("Seat not found");
                return response;
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error retrieving seat");
            return response;
        }
    }

    public Response<User> getUser(String email) {

        Response<User> response = new Response<User>();
        try {
            Statement stmt = getStatement();
            String sql = "SELECT * FROM user WHERE email = '" + email + "'";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setCity(rs.getString("city"));
                user.setProvince(rs.getString("province"));
                user.setPostalCode(rs.getString("postal_code"));
                response.setSuccess(true);
                response.setMessage("User retrieved successfully");
                response.setData(user);
                return response;
            } else {
                response.setSuccess(false);
                response.setMessage("User not found");
                return response;
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error retrieving user");
            return response;
        }

    }

    public Response<List<Ticket>> addTickets(ArrayList<Ticket> tickets) {

        Response<List<Ticket>> response = new Response<List<Ticket>>();
        try {

            Statement stmt = getStatement();
            String sql = "INSERT INTO ticket (showtime_id, seat_id, user_id, price, status) VALUES ";
            for (int i = 0; i < tickets.size(); i++) {
                Ticket ticket = tickets.get(i);
                sql += "(" + ticket.getShowtime().getId() + "," + ticket.getSeat().getId() + "," + ticket.getUserId()
                        + ","
                        + ticket.getPrice() + ",'" + ticket.getStatus() + "')";
                if (i < tickets.size() - 1) {
                    sql += ",";
                }
            }
            stmt.executeUpdate(sql);
            for (int i = 0; i < tickets.size(); i++) {
                Ticket ticket = tickets.get(i);
                sql = "UPDATE seat SET status = 'booked' WHERE id = " + ticket.getSeat().getId();
                stmt.executeUpdate(sql);
            }
            response.setSuccess(true);
            response.setMessage(
                    "Tickets added successfully \n " +
                            "if you want to cancel your tickets use the cancel ticket method up top \n" +
                            " or from my tickets page if you are logged in user");
            response.setData(tickets);
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error adding tickets");
            return response;
        }
    }

    public Response<List<Ticket>> getTicketsByUserEmail(String name) {

        Response<List<Ticket>> response = new Response<List<Ticket>>();
        try {
            Statement stmt = getStatement();
            String sql = "SELECT * FROM ticket join user on ticket.user_id = user.id join showtime on ticket.showtime_id = showtime.id join movie on showtime.movie_id = movie.id join seat on ticket.seat_id = seat.id  join theater on seat.theater_id = theater.id WHERE user.email = '"
                    + name + "'";
            ResultSet rs = stmt.executeQuery(sql);
            List<Ticket> tickets = new ArrayList<Ticket>();
            while (rs.next()) {
                Movie movie = new Movie();
                movie.setId(rs.getInt("movie.id"));
                movie.setTitle(rs.getString("movie.title"));

                Theater theater = new Theater();
                theater.setId(rs.getInt("theater.id"));
                theater.setName(rs.getString("theater.name"));
                theater.setAddress(rs.getString("theater.address"));

                Showtime showtime = new Showtime();
                showtime.setId(rs.getInt("showtime.id"));
                showtime.setMovie(movie);
                showtime.setTheater(theater);
                showtime.setTime(rs.getString("showtime.time"));
                Seat seat = new Seat();
                seat.setId(rs.getInt("seat.id"));
                seat.setRow_number(rs.getInt("seat.row_number"));
                seat.setSeat_number(rs.getInt("seat.seat_number"));

                Ticket ticket = new Ticket();
                ticket.setId(rs.getInt("ticket.id"));
                ticket.setShowtime(showtime);
                ticket.setSeat(seat);
                ticket.setUserId(rs.getInt("ticket.user_id"));
                ticket.setPrice(rs.getDouble("ticket.price"));
                ticket.setStatus(rs.getString("ticket.status"));
                tickets.add(ticket);
            }
            response.setSuccess(true);
            response.setMessage("Tickets retrieved successfully");
            response.setData(tickets);
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error retrieving tickets");
            return response;
        }
    }

}
