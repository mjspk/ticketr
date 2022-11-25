package com.ensf614.ticketr.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ensf614.ticketr.data.DataStore;
import com.ensf614.ticketr.model.*;
import com.ensf614.ticketr.model.Response;

@Controller
public class SelectionController {

    @Autowired
    DataStore dataStore;

    @RequestMapping("/select/{movieId}")
    public String buyTickets(Model model, @PathVariable int movieId, Selection selection) {
        Response<Movie> response = dataStore.getMovie(movieId);
        Response<ArrayList<Theater>> response2 = dataStore.getAllTheaters(movieId);
        ArrayList<Showtime> showtimes = null;
        ArrayList<Theater> theaters = null;
        ArrayList<Seat> seats = null;
        if (response.isSuccess()) {
            if (response2.isSuccess()) {
                theaters = response2.getData();
                int theaterId = selection.getSelectedTheaterId() == 0 ? theaters.get(0).getId()
                        : selection.getSelectedTheaterId();
                selection.setSelectedTheaterId(theaterId);
                Response<ArrayList<Showtime>> response3 = dataStore.getShowtimes(movieId, theaterId);
                if (response3.isSuccess()) {
                    showtimes = response3.getData();
                    if (showtimes.size() > 0) {
                        int showtimeId = selection.getSelectedShowtimeId() == 0 ? showtimes.get(0).getId()
                                : selection.getSelectedShowtimeId();
                        selection.setSelectedShowtimeId(showtimeId);
                        Response<ArrayList<Seat>> response4 = dataStore.getSeats(showtimeId);
                        if (response4.isSuccess()) {
                            seats = response4.getData();
                        }
                    }
                    selection.setSelectedMovie(response.getData());
                    model.addAttribute("selection", selection);
                    model.addAttribute("theaters", theaters);
                    model.addAttribute("showtimes", showtimes);
                    model.addAttribute("seats", seats);
                    return "select";
                } else {
                    model.addAttribute("message", response3.getMessage());
                    return "error";
                }
            } else {
                model.addAttribute("message", response2.getMessage());
                return "error";
            }
        } else {
            model.addAttribute("message", response.getMessage());
            return "error";
        }
    }

    @RequestMapping("/userinfo")
    public String userInfo(Model model, Selection selection) {
        if (selection.getSelectedSeats().length == 0) {
            model.addAttribute("message", "Please select at least one seat.");
            return "error";
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getName() != "anonymousUser") {
            Response<User> response = dataStore.getUser(auth.getName());
            if (response.isSuccess()) {
                selection.setUser(response.getData());
                model.addAttribute("selection", selection);
                return "checkout";
            } else {
                model.addAttribute("message", response.getMessage());
                return "error";
            }
        } else {
            model.addAttribute("selection", selection);
            return "userinfo";
        }

    }
}
