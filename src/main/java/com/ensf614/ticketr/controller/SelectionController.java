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
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ensf614.ticketr.data.DataStore;
import com.ensf614.ticketr.model.*;
import com.ensf614.ticketr.model.Response;

@Controller
public class SelectionController {

    @Autowired
    DataStore dataStore;

    @RequestMapping("/select/{movieId}")
    public String buyTickets(HttpSession session, Model model, @PathVariable int movieId) {
        Selection selection = (Selection) session.getAttribute("selection");

        if (selection == null) {
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
        if(selection.getSelectedTheaterId()==0){
            selection.setSelectedTheater(selection.getTheaters().get(0));
        }

        Response<ArrayList<Showtime>> response3 = dataStore.getShowtimes(movieId, selection.getSelectedTheaterId());
        if (response3.isSuccess()) {
            selection.setShowtimes(response3.getData());
            if(selection.getSelectedShowtimeId()==0){
                selection.setSelectedShowtime(selection.getShowtimes().get(0));
            }
            Response<ArrayList<Seat>> response4 = dataStore.getSeats(selection.getSelectedShowtimeId());
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
    public String userInfo(HttpSession session, Model model,Selection myselection) {
        Selection selection = (Selection) session.getAttribute("selection");
        if (selection == null) {
            return "redirect:/";
        }
        selection.setSelectedSeatsString(myselection.getSelectedSeatsString());
        if (selection.getSelectedSeatsString().length== 0) {
            model.addAttribute("message", "Please select at least one seat.");
            return "error";
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getName() != "anonymousUser") {
            Response<User> response = dataStore.getUser(auth.getName());
            if (response.isSuccess()) {
                selection.setUser(response.getData());
                session.setAttribute("selection", selection);
                return "redirect:/checkout";
            } else {
                model.addAttribute("message", response.getMessage());
                return "error";
            }
        } else {
            session.setAttribute("selection", selection);
            model.addAttribute("selection", selection);
            return "userinfo";
        }

    }
}
