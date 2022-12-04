package com.ensf614.ticketr.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.catalina.startup.ClassLoaderFactory.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import com.ensf614.ticketr.model.Card;
import com.ensf614.ticketr.model.Movie;
import com.ensf614.ticketr.model.News;
import com.ensf614.ticketr.model.Offer;
import com.ensf614.ticketr.model.Receipt;
import com.ensf614.ticketr.model.Response;
import com.ensf614.ticketr.model.Role;
import com.ensf614.ticketr.model.Seat;
import com.ensf614.ticketr.model.Selection;
import com.ensf614.ticketr.model.Showtime;
import com.ensf614.ticketr.model.Theater;
import com.ensf614.ticketr.model.Ticket;
import com.ensf614.ticketr.model.User;
import com.ensf614.ticketr.service.PaymentService;

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

    private PreparedStatement getPreparedStatement(String query) throws SQLException, ClassNotFoundException {
        Connection conn = DriverManager.getConnection(env.getProperty("db-url"), env.getProperty("db-username"),
                env.getProperty("db-password"));
        PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
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

            String sql = "INSERT INTO user (first_name, last_name, email, address, city, province, postal_code, phone, password) VALUES ('"
                    + user.getFirstName() + "', '" + user.getLastName() + "', '" + user.getEmail() + "', '"
                    + user.getAddress() + "', '" + user.getCity() + "', '" + user.getProvince() + "', '"
                    + user.getPostalCode() + "', '" + user.getPhone() + "', '" + user.getPassword() + "')";
            PreparedStatement stmt = getPreparedStatement(sql);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getInt(1));
            }
            // add user card to database
            if (user.getDefaultCard() != null) {
                sql = "INSERT INTO card (user_id, card_number, card_name, card_expiry, card_cvv) VALUES ('"
                        + user.getId()
                        + "', '" + user.getDefaultCard().getCardNumber() + "', '"
                        + user.getDefaultCard().getCardHolderName()
                        + "', '" + user.getDefaultCard().getExpiryDate() + "', '" + user.getDefaultCard().getCvv()
                        + "')";
                stmt = getPreparedStatement(sql);
                stmt.executeUpdate();
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    user.getDefaultCard().setId(rs.getInt(1));
                }
            }
            editRoles(user);
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

    @Override
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
            String sql = "INSERT INTO user (first_name, last_name, email, address, city, province, postal_code, phone, password) VALUES ('"
                    + user.getFirstName() + "', '" + user.getLastName() + "', '" + user.getEmail() + "', '"
                    + user.getAddress() + "', '" + user.getCity() + "', '" + user.getProvince() + "', '"
                    + user.getPostalCode() + "', '" + user.getPhone() + "', '" + user.getPassword() + "')";

            PreparedStatement praperedStmt = getPreparedStatement(sql);
            praperedStmt.executeUpdate();
            ResultSet rs = praperedStmt.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getInt(1));
            }
            editRoles(user);
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
                user.setAddress(rs.getString("address"));
                user.setCity(rs.getString("city"));
                user.setProvince(rs.getString("province"));
                user.setPostalCode(rs.getString("postal_code"));
                user.setPhone(rs.getString("phone"));
                user.setRoles(getUserRoles(user));
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
                movie.setTrailer(rs.getString("trailer"));
                movie.setStatus(rs.getString("status"));
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
    public Response<ArrayList<Movie>> getAllShowingMovies() {
        Response<ArrayList<Movie>> response = new Response<ArrayList<Movie>>();
        try {
            Statement stmt = getStatement();
            String sql = "SELECT * FROM movie where status = 'showing'";
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
                movie.setTrailer(rs.getString("trailer"));
                movie.setStatus(rs.getString("status"));

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
    public Response<ArrayList<Movie>> getUpcomingMovies() {
        Response<ArrayList<Movie>> response = new Response<ArrayList<Movie>>();
        try {
            Statement stmt = getStatement();
            String sql = "SELECT * FROM movie WHERE status = 'upcoming'";
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
                movie.setTrailer(rs.getString("trailer"));
                movie.setStatus(rs.getString("status"));

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

    private Set<Role> getUserRoles(User user) {
        try {
            Statement stmt = getStatement();
            String sql = "SELECT * FROM user_role join role on user_role.role_id = role.id WHERE user_id = "
                    + user.getId();
            ResultSet rs = stmt.executeQuery(sql);
            Set<Role> roles = new HashSet<Role>();
            while (rs.next()) {
                Role role = new Role();
                role.setId(rs.getInt("role.id"));
                role.setName(rs.getString("name"));
                roles.add(role);
            }
            return roles;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
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
                movie.setTrailer(rs.getString("trailer"));
                movie.setStatus(rs.getString("status"));

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

    @Override
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

    @Override
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

    @Override
    public Response<Showtime> getShowtime(int showtimeId) {

        Response<Showtime> response = new Response<Showtime>();
        try {
            Statement stmt = getStatement();
            String sql = "SELECT * FROM showtime join theater on showtime.theater_id = theater.id join movie on showtime.movie_id = movie.id WHERE showtime.id = "
                    + showtimeId;
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                Movie movie = new Movie();
                movie.setId(rs.getInt("movie.id"));
                movie.setTitle(rs.getString("movie.title"));
                movie.setGenre(rs.getString("movie.genre"));
                movie.setRating(rs.getString("movie.rating"));
                movie.setDuration(rs.getString("movie.duration"));
                movie.setReleaseDate(rs.getString("movie.release_date"));
                movie.setPoster(rs.getString("movie.poster"));

                Theater theater = new Theater();
                theater.setId(rs.getInt("theater.id"));
                theater.setName(rs.getString("theater.name"));

                Showtime showtime = new Showtime();
                showtime.setId(rs.getInt("id"));
                showtime.setTime(rs.getString("time"));
                showtime.setDate(rs.getString("date"));
                showtime.setMovie(movie);
                showtime.setTheater(theater);
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

    @Override
    public Response<ArrayList<Seat>> getSeats(int movieId, int showtimeId) {

        Response<ArrayList<Seat>> response = new Response<ArrayList<Seat>>();
        try {
            Statement stmt = getStatement();

            String sql = "SELECT seat.id, row_number, seat_number,(CASE WHEN ticket.id IS NULL THEN 'available' ELSE 'booked' END) AS status FROM seat join showtime on seat.theater_id = showtime.theater_id LEFT JOIN ticket on seat.id = ticket.seat_id AND showtime.id = ticket.showtime_id AND ticket.status != 'cancelled' WHERE showtime.id = "
                    + showtimeId;
            ResultSet rs = stmt.executeQuery(sql);
            ArrayList<Seat> seats = new ArrayList<Seat>();
            while (rs.next()) {
                Seat seat = new Seat();
                seat.setId(rs.getInt("id"));
                seat.setRow_number(rs.getInt("row_number"));
                seat.setSeat_number(rs.getInt("seat_number"));
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

    @Override
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

    @Override
    public Response<ArrayList<Ticket>> addTickets(ArrayList<Ticket> tickets, int userId) {

        Response<ArrayList<Ticket>> response = new Response<ArrayList<Ticket>>();
        try {

            String sql = "INSERT INTO ticket (showtime_id, seat_id, user_id, price, status) VALUES ";
            for (int i = 0; i < tickets.size(); i++) {
                Ticket ticket = tickets.get(i);
                ticket.setStatus("reserved");
                sql += "(" + ticket.getShowtime().getId() + ", " + ticket.getSeat().getId() + ", "
                        + userId + ", " + ticket.getPrice() + ", '" + ticket.getStatus() + "')";
                if (i < tickets.size() - 1) {
                    sql += ", ";
                }
            }
            PreparedStatement praperedStmt = getPreparedStatement(sql);
            praperedStmt.executeUpdate();
            praperedStmt.clearBatch();
            ArrayList<Integer> ticketIds = new ArrayList<Integer>();
            ResultSet rs = praperedStmt.getGeneratedKeys();
            while (rs.next()) {
                ticketIds.add(rs.getInt(1));
            }
            for (int i = 0; i < tickets.size(); i++) {
                tickets.get(i).setId(ticketIds.get(i));
            }

            response.setSuccess(true);
            response.setMessage("Tickets added successfully");
            response.setData(tickets);
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error adding tickets");
            return response;
        }
    }

    @Override
    public Response<List<Ticket>> getTicketsByUserEmail(String name) {

        Response<List<Ticket>> response = new Response<List<Ticket>>();
        try {
            Statement stmt = getStatement();
            String sql = "SELECT * FROM ticket join user on ticket.user_id = user.id join showtime on ticket.showtime_id = showtime.id join movie on showtime.movie_id = movie.id join seat on ticket.seat_id = seat.id  join theater on seat.theater_id = theater.id WHERE user.email = '"
                    + name + "' AND ticket.status != 'cancelled'";
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

    @Override
    public Response<ArrayList<News>> getMoviesNews() {
        Response<ArrayList<News>> response = new Response<>();
        try {
            Statement stmt = getStatement();
            String sql = "SELECT * FROM news";
            ResultSet rs = stmt.executeQuery(sql);
            ArrayList<News> mynews = new ArrayList<News>();
            while (rs.next()) {
                News news = new News();
                news.setImage(rs.getString("image"));
                news.setTheNews(rs.getString("theNews"));
                news.setTitle(rs.getString("title"));
                mynews.add(news);
            }
            response.setSuccess(true);
            response.setMessage("News retrieved successfully");
            response.setData(mynews);
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error retrieving news");
            return response;
        }
    }

    @Override
    public Response<Card> getDefaultCard(User user) {
        Response<Card> response = new Response<>();
        try {
            Statement stmt = getStatement();
            String sql = "SELECT * FROM card WHERE user_id = " + user.getId() + " ";
            ResultSet rs = stmt.executeQuery(sql);
            Card card = new Card();
            if (rs.next()) {
                card.setId(rs.getInt("id"));
                card.setCardNumber(rs.getString("card_number"));
                card.setCvv(rs.getString("cvv"));
                card.setExpiryDate(rs.getString("expiration_date"));
                card.setUserId(rs.getInt("user_id"));
                response.setSuccess(true);
                response.setMessage("Card retrieved successfully");
                response.setData(card);
                return response;
            } else {
                response.setSuccess(false);
                response.setMessage("Card not found");
                return response;
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error retrieving card");
            return response;
        }
    }

    @Override
    public boolean isEmailInUse(String email) {
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
    public boolean checkSeats(int selectedShowtimeId, String[] selectedSeatsString) {

        try {
            Statement stmt = getStatement();
            String sql = "SELECT * FROM seat WHERE showtime_id = " + selectedShowtimeId + " AND status = 'booked'";
            ResultSet rs = stmt.executeQuery(sql);
            List<Seat> bookedSeats = new ArrayList<Seat>();
            while (rs.next()) {
                Seat seat = new Seat();
                seat.setId(rs.getInt("id"));
                seat.setRow_number(rs.getInt("row_number"));
                seat.setSeat_number(rs.getInt("seat_number"));
                seat.setStatus(rs.getString("status"));
                bookedSeats.add(seat);
            }
            for (String seat : selectedSeatsString) {
                int seat_id = Integer.parseInt(seat);
                for (Seat bookedSeat : bookedSeats) {
                    if (bookedSeat.getId() == seat_id) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean ifSeatsExceedsTenPercentage(int movieId, int theaterId, int showtimeId,
            String[] selectedSeatsString) {
        try {

            Statement stmt = getStatement();
            String sql = "SELECT status FROM movie WHERE id = " + movieId + " and status = 'upcoming'";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                sql = "SELECT * FROM seat join ticket on seat.id = ticket.seat_id join theater on theater.id = seat.theater_id join showtime on theater.id = showtime.theater_id join movie on movie.id = showtime.movie_id WHERE movie.id = "
                        + movieId + " AND theater.id = " + theaterId + " AND showtime.id = " + showtimeId
                        + " AND seat.status = 'booked'";
                List<Seat> bookedSeats = new ArrayList<Seat>();
                while (rs.next()) {
                    Seat seat = new Seat();
                    seat.setId(rs.getInt("id"));
                    seat.setRow_number(rs.getInt("row_number"));
                    seat.setSeat_number(rs.getInt("seat_number"));
                    seat.setStatus(rs.getString("status"));
                    bookedSeats.add(seat);
                }
                sql = "SELECT * FROM seat join theater on theater.id = seat.theater_id join showtime on theater.id = showtime.theater_id join movie on movie.id = showtime.movie_id WHERE movie.id = "
                        + movieId + " AND theater.id = " + theaterId + " AND showtime.id = " + showtimeId;
                rs = stmt.executeQuery(sql);
                List<Seat> allSeats = new ArrayList<Seat>();
                while (rs.next()) {
                    Seat seat = new Seat();
                    seat.setId(rs.getInt("id"));
                    seat.setRow_number(rs.getInt("row_number"));
                    seat.setSeat_number(rs.getInt("seat_number"));
                    seat.setStatus(rs.getString("status"));
                    allSeats.add(seat);
                }
                int bookedSeatsCount = bookedSeats.size();
                int allSeatsCount = allSeats.size();
                int selectedSeatsCount = selectedSeatsString.length;
                int totalSeatsCount = bookedSeatsCount + selectedSeatsCount;
                double percentage = (double) totalSeatsCount / allSeatsCount;
                if (percentage > 0.1) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Response<Card> updateUserPayment(int userId, Card card) {
        Response<Card> response = new Response<>();
        try {
            Statement stmt = getStatement();
            // update card if it exists else create a new one
            String sql = "SELECT * FROM card WHERE user_id = " + userId + " ";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                sql = "UPDATE card SET card_number = '" + card.getCardNumber() + "', card_cvv = '" + card.getCvv()
                        + "', card_expiry = '" + card.getExpiryDate() + "',card_name = '" + card.getCardHolderName()
                        + "' WHERE user_id = " + userId + " ";
                stmt.executeUpdate(sql);
            } else {
                sql = "INSERT INTO card (card_number, card_cvv, card_expiry, card_name, user_id) VALUES ('"
                        + card.getCardNumber() + "', '" + card.getCvv() + "', '" + card.getExpiryDate() + "', '"
                        + card.getCardHolderName() + "', " + userId + ")";
                stmt.executeUpdate(sql);
            }
            stmt.executeUpdate(sql);
            response.setSuccess(true);
            response.setMessage("Card updated successfully");
            response.setData(card);
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error updating user");
            return response;
        }
    }

    @Override
    public Response<ArrayList<Movie>> searchMovies(String query) {

        Response<ArrayList<Movie>> response = new Response<>();
        try {
            Statement stmt = getStatement();
            String sql = "SELECT * FROM movie WHERE title LIKE '%" + query + "%' OR description LIKE '%" + query
                    + "%' OR genre LIKE '%" + query + "%' OR rating LIKE '%" + query + "%' OR release_date LIKE '%"
                    + query + "%' OR duration LIKE '%" + query + "%' OR trailer LIKE '%" + query
                    + "%' OR poster LIKE '%"
                    + query + "%' OR status LIKE '%" + query + "%'";
            ResultSet rs = stmt.executeQuery(sql);
            ArrayList<Movie> movies = new ArrayList<Movie>();
            while (rs.next()) {
                Movie movie = new Movie();
                movie.setId(rs.getInt("id"));
                movie.setTitle(rs.getString("title"));
                movie.setDescription(rs.getString("description"));
                movie.setGenre(rs.getString("genre"));
                movie.setRating(rs.getString("rating"));
                movie.setReleaseDate(rs.getString("release_date"));
                movie.setDuration(rs.getString("duration"));
                movie.setTrailer(rs.getString("trailer"));
                movie.setPoster(rs.getString("poster"));
                movie.setStatus(rs.getString("status"));
                movies.add(movie);
            }
            response.setSuccess(true);
            response.setMessage("Movies fetched successfully");
            response.setData(movies);
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error fetching movies");
            return response;
        }
    }

    @Override
    public Response<Receipt> processPayment(Selection selection) {

        Response<Receipt> response = new Response<>();
        try {
            String transaction_id = PaymentService.getInstance().processPayment(selection.getCard().getCardNumber(),
                    selection.getCard().getCardHolderName(), selection.getCard().getExpiryDate(),
                    selection.getCard().getCvv());
            String sql = "INSERT INTO payment (user_id, transaction_id, amount) VALUES (" + selection.getUser().getId()
                    + ", '" + transaction_id + "', " + selection.getTotalPrice() + ")";
            PreparedStatement stmt = getPreparedStatement(sql);
            stmt.executeUpdate();
            stmt.clearBatch();
            Receipt receipt = new Receipt();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                receipt.setId(rs.getInt(1));
            }
            receipt.setDate(selection.getSelectedShowtime().getDate());
            receipt.setTime(selection.getSelectedShowtime().getTime());
            receipt.setMovieName(selection.getSelectedMovie().getTitle());
            receipt.setTheatreName(selection.getSelectedTheater().getName());
            receipt.setUserEmail(selection.getUser().getEmail());
            receipt.setPrice(selection.getTotalPrice());
            receipt.setTickets(selection.getSelectedTickets());

            response.setSuccess(true);
            response.setMessage("Receipt fetched successfully");
            response.setData(receipt);
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error fetching receipt");
            return response;
        }

    }

    @Override
    public Response<Boolean> addMovie(Movie movie) {

        Response<Boolean> response = new Response<>();
        try {
            String sql = "INSERT INTO movie (title, description, genre, rating, release_date, duration, trailer, poster, status) VALUES ('"
                    + movie.getTitle() + "', '" + movie.getDescription() + "', '" + movie.getGenre() + "', '"
                    + movie.getRating() + "', '" + movie.getReleaseDate() + "', '" + movie.getDuration() + "', '"
                    + movie.getTrailer() + "', '" + movie.getPoster() + "', '" + movie.getStatus() + "')";
            PreparedStatement stmt = getPreparedStatement(sql);
            stmt.executeUpdate();
            stmt.clearBatch();
            response.setSuccess(true);
            response.setMessage("Movie added successfully");
            response.setData(true);
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error adding movie");
            return response;
        }

    }

    @Override
    public Response<Boolean> deleteMovie(int id) {

        Response<Boolean> response = new Response<>();
        try {

            Statement stmt = getStatement();
            String sql = "DELETE FROM showtime WHERE showtime.movie_id = "
                    + id;
            stmt.executeUpdate(sql);
            sql = "DELETE FROM movie WHERE movie.id = "
                    + id;
            stmt.executeUpdate(sql);
            response.setSuccess(true);
            response.setMessage("Movie deleted successfully");
            response.setData(true);
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error deleting movie");
            return response;
        }

    }

    @Override
    public Response<Boolean> deleteUser(int id) {

        Response<Boolean> response = new Response<>();
        try {

            Statement stmt = getStatement();
            String sql = "DELETE FROM card WHERE user_id = " + id;
            stmt.executeUpdate(sql);
            sql = "DELETE FROM payment WHERE user_id = " + id;
            stmt.executeUpdate(sql);
            sql = "DELETE FROM ticket WHERE user_id = " + id;
            stmt.executeUpdate(sql);
            sql = "DELETE FROM user WHERE id = " + id;
            stmt.executeUpdate(sql);
            response.setSuccess(true);
            response.setMessage("User deleted successfully");
            response.setData(true);
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error deleting user");
            return response;
        }

    }

    @Override
    public Response<ArrayList<User>> getAllUsers() {

        Response<ArrayList<User>> response = new Response<>();
        try {
            String sql = "SELECT * FROM user";
            Statement stmt = getStatement();
            ResultSet rs = stmt.executeQuery(sql);
            ArrayList<User> users = new ArrayList<User>();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setAddress(rs.getString("address"));
                user.setCity(rs.getString("city"));
                user.setProvince(rs.getString("province"));
                user.setPostalCode(rs.getString("postal_code"));
                user.setPhone(rs.getString("phone"));
                user.setPassword(rs.getString("password"));
                users.add(user);
            }
            for (User user : users) {
                sql = "SELECT * FROM user_role join role on user_role.role_id = role.id WHERE user_id = "
                        + user.getId();
                rs = stmt.executeQuery(sql);
                Set<Role> roles = new HashSet<Role>();
                while (rs.next()) {
                    Role role = new Role();
                    role.setId(rs.getInt("id"));
                    role.setName(rs.getString("name"));
                    roles.add(role);
                }
                user.setRoles(roles);
            }
            response.setSuccess(true);
            response.setMessage("Users fetched successfully");
            response.setData(users);
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error fetching users");
            return response;
        }
    }

    @Override
    public Response<Card> getDefaultCard(int id) {

        Response<Card> response = new Response<>();
        try {
            String sql = "SELECT * FROM card WHERE user_id = " + id;
            Statement stmt = getStatement();
            ResultSet rs = stmt.executeQuery(sql);
            Card card = new Card();
            if (rs.next()) {
                card.setId(rs.getInt("id"));
                card.setUserId(rs.getInt("user_id"));
                card.setCardNumber(rs.getString("card_number"));
                card.setCardHolderName(rs.getString("card_name"));
                card.setExpiryDate(rs.getString("card_expiry"));
                card.setCvv(rs.getString("card_cvv"));
            }
            response.setSuccess(true);
            response.setMessage("Default card fetched successfully");
            response.setData(card);
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error fetching default card");
            return response;
        }

    }

    @Override
    public Response<Boolean> editRoles(User user) {

        Response<Boolean> response = new Response<>();
        try {
            String sql = "DELETE FROM user_role WHERE user_id = " + user.getId();
            PreparedStatement stmt = getPreparedStatement(sql);
            stmt.executeUpdate();
            stmt.clearBatch();
            for (String roleName : user.getRolStrings()) {
                sql = "INSERT INTO user_role (user_id, role_id) VALUES (" + user.getId()
                        + ", (SELECT id FROM role WHERE name = '"
                        + roleName + "'))";
                stmt = getPreparedStatement(sql);
                stmt.executeUpdate();
                stmt.clearBatch();
            }
            response.setSuccess(true);
            response.setMessage("Roles updated successfully");
            response.setData(true);
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error updating roles");
            return response;
        }
    }

    @Override
    public ArrayList<Role> getAllRoles() {
        try {
            Statement stmt = getStatement();
            String sql = "SELECT * FROM role";
            ResultSet rs = stmt.executeQuery(sql);
            ArrayList<Role> roles = new ArrayList<Role>();
            while (rs.next()) {
                Role role = new Role();
                role.setId(rs.getInt("id"));
                role.setName(rs.getString("name"));
                roles.add(role);
            }
            return roles;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Response<User> getUser(int id) {

        Response<User> response = new Response<>();
        try {
            String sql = "SELECT * FROM user WHERE id = " + id;
            Statement stmt = getStatement();
            ResultSet rs = stmt.executeQuery(sql);
            User user = new User();
            while (rs.next()) {
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setAddress(rs.getString("address"));
                user.setCity(rs.getString("city"));
                user.setProvince(rs.getString("province"));
                user.setPostalCode(rs.getString("postal_code"));
                user.setPhone(rs.getString("phone"));
                user.setPassword(rs.getString("password"));
                user.setRoles(getUserRoles(user));
            }
            response.setSuccess(true);
            response.setMessage("User fetched successfully");
            response.setData(user);
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error fetching user");
            return response;
        }
    }

    @Override
    public Response<User> updateUser(User user) {
        Response<User> response = new Response<>();
        try {
            String sql = "UPDATE user SET first_name = '" + user.getFirstName() + "', last_name = '"
                    + user.getLastName() + "', email = '" + user.getEmail() + "', address = '"
                    + user.getAddress() + "', city = '" + user.getCity() + "', province = '"
                    + user.getProvince() + "', postal_code = '" + user.getPostalCode() + "', phone = '"
                    + user.getPhone() + "', password = '" + user.getPassword() + "' WHERE id = "
                    + user.getId();
            PreparedStatement stmt = getPreparedStatement(sql);
            stmt.executeUpdate();
            stmt.clearBatch();
            editRoles(user);
            response.setSuccess(true);
            response.setMessage("User updated successfully");
            response.setData(user);
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error updating user");
            return response;
        }
    }

    @Override
    public Response<ArrayList<Offer>> getOffers() {

        Response<ArrayList<Offer>> response = new Response<>();
        try {

            String sql = "SELECT * FROM offer o INNER JOIN movie m ON o.movie_id = m.id";
            Statement stmt = getStatement();
            ResultSet rs = stmt.executeQuery(sql);
            ArrayList<Offer> offers = new ArrayList<Offer>();
            while (rs.next()) {
                Offer offer = new Offer();
                offer.setId(rs.getInt("id"));
                offer.setUserId(rs.getInt("user_id"));
                offer.setPrice(rs.getDouble("price"));
                offer.setOffer(rs.getString("offer"));
                offer.setQuantity(rs.getInt("quantity"));
                Movie movie = new Movie();
                movie.setId(rs.getInt("movie_id"));
                movie.setTitle(rs.getString("title"));
                offer.setMovie(movie);

            }
            response.setSuccess(true);
            response.setMessage("Offers fetched successfully");
            response.setData(offers);
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error fetching offers");
            return response;
        }
    }

    @Override
    public Response<Boolean> deleteTicket(int id) {

        Response<Boolean> response = new Response<>();
        try {

            Statement stmt = getStatement();
            String sql = "DELETE FROM ticket WHERE id = "
                    + id;
            stmt.executeUpdate(sql);
            response.setSuccess(true);
            response.setMessage("Ticket deleted successfully");
            response.setData(true);
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error deleting ticket");
            return response;
        }

    }

    @Override
    public Response<Boolean> cancelTicket(Ticket ticket) {

        Response<Boolean> response = new Response<>();
        try {

            // chedk show time is not 72 hours way from now
            String sql = "SELECT * FROM showtime WHERE id = " + ticket.getShowtime().getId();
            Statement stmt = getStatement();
            ResultSet rs = stmt.executeQuery(sql);
            Showtime showtime = new Showtime();
            while (rs.next()) {
                showtime.setId(rs.getInt("id"));
                showtime.setTime(rs.getString("time"));
                showtime.setDate(rs.getString("date"));
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = sdf.parse(showtime.getDate() + " " + showtime.getTime());
            Date now = new Date();
            long diff = date.getTime() - now.getTime();
            long diffHours = diff / (60 * 60 * 1000);
            if (diffHours < 72) {
                response.setSuccess(false);
                response.setMessage("You can only cancel tickets 72 hours before showtime");
                return response;
            }

            sql = "UPDATE ticket SET status = 'cancelled' WHERE id = " + ticket.getId();
            stmt = getStatement();
            stmt.executeUpdate(sql);
            response.setSuccess(true);
            response.setMessage("Ticket cancelled successfully");
            response.setData(true);
            return response;

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error deleting ticket");
            return response;
        }

    }

    @Override
    public Response<Ticket> getTicket(int id) {

        Response<Ticket> response = new Response<Ticket>();
        try {
            Statement stmt = getStatement();
            String sql = "SELECT * FROM ticket join showtime on ticket.showtime_id = showtime.id WHERE ticket.id = "
                    + id;
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                Ticket ticket = new Ticket();
                ticket.setId(rs.getInt("ticket.id"));
                ticket.setUserId(rs.getInt("ticket.user_id"));
                ticket.setPrice(rs.getDouble("price"));
                ticket.setStatus(rs.getString("status"));
                Showtime showtime = new Showtime();
                showtime.setId(rs.getInt("showtime.id"));
                ticket.setShowtime(showtime);
                response.setSuccess(true);
                response.setMessage("Ticket retrieved successfully");
                response.setData(ticket);
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

    @Override
    public Response<User> getUserByEmail(String email) {

        Response<User> response = new Response<User>();
        try {
            Statement stmt = getStatement();
            String sql = "SELECT * FROM user WHERE email = '"
                    + email + "'";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("user.id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                Set<Role> roles = getUserRoles(user);
                user.setRoles(roles);
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

    @Override
    public Response<Boolean> addUserCredit(int userId, double amount) {
        Response<Boolean> response = new Response<>();
        try {
            String sql = "INSERT INTO credit (user_id, credit_amount,expiration_date) VALUES ("
                    + userId + ", " + amount + ", DATE_ADD(NOW(), INTERVAL 1 YEAR))";
            PreparedStatement stmt = getPreparedStatement(sql);
            stmt.executeUpdate();
            stmt.clearBatch();
            response.setSuccess(true);
            response.setMessage("Credit added successfully");
            response.setData(true);
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error adding credit");
            return response;
        }
    }

}
