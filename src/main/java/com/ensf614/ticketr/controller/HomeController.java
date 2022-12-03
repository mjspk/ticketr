package com.ensf614.ticketr.controller;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ensf614.ticketr.data.DataStore;
import com.ensf614.ticketr.data.IDataStore;
import com.ensf614.ticketr.model.*;
import com.ensf614.ticketr.model.Response;

@Controller
public class HomeController {

    @Autowired
    IDataStore dataStore;

    @RequestMapping("/")
    public String home(Model model) {
        Response<ArrayList<Movie>> response = dataStore.getAllShowingMovies();
        if (response.isSuccess()) {
            model.addAttribute("movies", response.getData());
            return "home";
        } else {
            model.addAttribute("message", response.getMessage());
            return "error";
        }
    }

    @RequestMapping("/home")
    public String home2(Model model) {
        Response<ArrayList<Movie>> response = dataStore.getAllMovies();
        if (response.isSuccess()) {
            model.addAttribute("movies", response.getData());
            return "home";
        } else {
            model.addAttribute("message", response.getMessage());
            return "error";
        }
    }

    // upcomming movies
    @RequestMapping("/upcoming")
    public String upcoming(Model model) {
        Response<ArrayList<Movie>> response = dataStore.getUpcomingMovies();
        if (response.isSuccess()) {
            model.addAttribute("movies", response.getData());
            return "upcoming";
        } else {
            model.addAttribute("message", response.getMessage());
            return "error";
        }
    }

    @RequestMapping("/search")
    public String search(@RequestParam("query") String query, Model model) {
        Response<ArrayList<Movie>> response = dataStore.searchMovies(query);
        if (response.isSuccess()) {
            model.addAttribute("movies", response.getData());
            return "home";
        } else {
            model.addAttribute("message", response.getMessage());
            return "error";
        }
    }

}