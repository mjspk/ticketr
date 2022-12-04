package com.ensf614.ticketr.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ensf614.ticketr.data.DataStore;
import com.ensf614.ticketr.data.IDataStore;
import com.ensf614.ticketr.model.Card;
import com.ensf614.ticketr.model.Movie;
import com.ensf614.ticketr.model.Receipt;
import com.ensf614.ticketr.model.Response;
import com.ensf614.ticketr.model.Role;
import com.ensf614.ticketr.model.Seat;
import com.ensf614.ticketr.model.Selection;
import com.ensf614.ticketr.model.Ticket;
import com.ensf614.ticketr.model.User;
import com.ensf614.ticketr.service.EmailService;

@PreAuthorize("hasRole('ROLE_ADMIN')")
@Controller
public class AdminController {
    @Autowired
    IDataStore dataStore;

    @RequestMapping("/admin")
    public String adminPage(Model model) {
        Response<ArrayList<User>> response = dataStore.getAllUsers();
        if (response.isSuccess()) {
            model.addAttribute("users", response.getData());
            model.addAttribute("user", new User());
            return "admin";
        } else {
            model.addAttribute("message", response.getMessage());
            return "error";
        }

    }

    @RequestMapping("/admin/adduser")
    public String addUser(Model model) {
        ArrayList<Role> roles = dataStore.getAllRoles();
        model.addAttribute("allRoles", roles);
        model.addAttribute("user", new User());
        return "adduser";
    }

    @PostMapping("/admin/adduser")
    public String addUser(User user, Model model) {

        user.setRolStrings(new String[] { "ROLE_USER" });
        Response<User> response = dataStore.addUser(user);
        if (response.isSuccess()) {
            return "redirect:/admin";
        } else {
            model.addAttribute("message", response.getMessage());
            return "error";
        }
    }

    @RequestMapping("/admin/edituser")
    public String editUser(@RequestParam("id") int id, Model model) {
        Response<User> response = dataStore.getUser(id);
        if (response.isSuccess()) {
            model.addAttribute("user", response.getData());
            ArrayList<Role> roles = dataStore.getAllRoles();
            model.addAttribute("allRoles", roles);
            return "edituser";
        } else {
            model.addAttribute("message", response.getMessage());
            return "error";
        }
    }

    @PostMapping("/admin/edituser")
    public String editUser(User user, Model model) {
        Response<User> response = dataStore.updateUser(user);
        if (response.isSuccess()) {
            return "redirect:/admin";
        } else {
            model.addAttribute("message", response.getMessage());
            return "error";
        }
    }

    @RequestMapping("/admin/showAllMovies")
    public String showAllMovies(Model model) {
        Response<ArrayList<Movie>> response = dataStore.getAllMovies();
        if (response.isSuccess()) {
            model.addAttribute("movies", response.getData());
        }
        return "adminmovies";
    }

    @RequestMapping("/admin/addmovie")
    public String addMovie(Model model) {
        model.addAttribute("movie", new Movie());
        return "addmovie";
    }

    @Autowired
    EmailService emailService;

    @PostMapping("/admin/addmovie")
    public String addMovie(Movie movie, Model model) {
        Response<Movie> response = dataStore.addMovie(movie);

        if (response.isSuccess()) {
            Response<List<String>> emails = dataStore.getAllUsersEmails();
            emailService.sendHtmlNewsletter(emails.getData(), " New Movie Added",
                    "A new movie has been added to the website. Check it out!");
            return "redirect:/admin/showAllMovies";
        } else {
            model.addAttribute("message", response.getMessage());
            return "addmovie";
        }
    }

    @RequestMapping("/admin/deletemovie")
    public String deleteMovie(Model model, @RequestParam("id") int id) {
        Response<Boolean> response = dataStore.deleteMovie(id);
        if (response.isSuccess()) {
            return "redirect:/admin/showAllMovies";

        } else {
            model.addAttribute("message", response.getMessage());
            return "adminmovies";
        }
    }

    @RequestMapping("/admin/deleteuser")
    public String deleteUser(Model model, @RequestParam("id") int id) {
        Response<Boolean> response = dataStore.deleteUser(id);
        if (response.isSuccess()) {
            return "redirect:/admin";
        } else {
            model.addAttribute("message", response.getMessage());
            return "error";
        }
    }

    @PostMapping("/admin/editRoles")
    public String editRoles(Model model, User user) {
        Response<Boolean> response = dataStore.editRoles(user);
        if (response.isSuccess()) {
            return "redirect:/admin";
        } else {
            model.addAttribute("message", response.getMessage());
            return "admin";
        }
    }

}
