package com.ensf614.ticketr.controller;

import java.util.ArrayList;
import javax.servlet.http.HttpSession;
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
    public String buyTickets(HttpSession session, Model model, @PathVariable int movieId, Selection mySelection) {
        Selection selection = (Selection) session.getAttribute("selection");

        if (selection == null || selection.getSelectedMovie().getId() != movieId) {
            selection = new Selection();
            Response<Movie> response = dataStore.getMovie(movieId);
            if (response.isSuccess()) {
                selection.setSelectedMovie(response.getData());
            } else {
                model.addAttribute("message", response.getMessage());
                return "error";
            }
            Response<ArrayList<Theater>> response2 = dataStore.getAllTheaters(movieId);
            if (response2.isSuccess()) {
                selection.setTheaters(response2.getData());
            } else {
                model.addAttribute("message", response.getMessage());
                return "error";
            }
        }

        if (selection.getSelectedTheaterId() == 0) {
            selection.setSelectedTheater(selection.getTheaters().get(0));
        }

        Response<ArrayList<Showtime>> response3 = dataStore.getShowtimes(movieId, selection.getSelectedTheaterId());
        if (response3.isSuccess()) {
            selection.setShowtimes(response3.getData());
            if (selection.getSelectedShowtimeId() == 0) {
                if (selection.getShowtimes().size() > 0) {
                    selection.setSelectedShowtime(selection.getShowtimes().get(0));
                } else {
                    model.addAttribute("message", "No showtimes available for this movie and theater");
                    return "error";
                }
            }
            Response<ArrayList<Seat>> response4 = dataStore.getSeats(movieId, selection.getSelectedShowtimeId());
            if (response4.isSuccess()) {
                selection.setSeats(response4.getData());
            } else {
                model.addAttribute("message", response4.getMessage());
                return "error";
            }
            model.addAttribute("selection", selection);
            session.setAttribute("selection", selection);
            return "select";
        } else {
            model.addAttribute("message", response3.getMessage());
            return "error";
        }
    }

    @RequestMapping("/userinfo")
    public String userInfo(HttpSession session, Model model, Selection myselection) {
        Selection selection = (Selection) session.getAttribute("selection");
        if (selection == null) {
            return "redirect:/";
        }
        selection.setSelectedSeatsString(myselection.getSelectedSeatsString());
        if (selection.getSelectedSeatsString().length == 0) {
            model.addAttribute("message", "Please select at least one seat.");
            return "error";
        }

        if (dataStore.checkSeats(selection.getSelectedShowtimeId(), selection.getSelectedSeatsString())) {
            model.addAttribute("message", "Sorry, one or more of the seats you selected are no longer available.");
            return "error";
        }

        if (dataStore.ifSeatsExceedsTenPercentage(selection.getSelectedMovie().getId(),
                selection.getSelectedTheaterId(), selection.getSelectedShowtimeId(),
                selection.getSelectedSeatsString())) {
            model.addAttribute("message",
                    "Sorry, one or more of the seats you selected exceeds 10% of the total seats for upcoming showtime.");
            return "error";
        }
        return "redirect:/checkout";


    }
}
