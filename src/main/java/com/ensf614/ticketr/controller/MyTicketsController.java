package com.ensf614.ticketr.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ensf614.ticketr.data.DataStore;
import com.ensf614.ticketr.data.IDataStore;
import com.ensf614.ticketr.model.Response;
import com.ensf614.ticketr.model.Ticket;
import com.ensf614.ticketr.model.User;

@Controller
public class MyTicketsController {
    @Autowired
    IDataStore dataStore;

    @RequestMapping("/mytickets")
    public String myTicketsPage(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Response<List<Ticket>> response = dataStore.getTicketsByUserEmail(auth.getName());
        if (response.isSuccess()) {
            model.addAttribute("tickets", response.getData());
            return "mytickets";
        } else {
            model.addAttribute("message", response.getMessage());
            return "error";
        }

    }

    @RequestMapping("/myticketsguest")
    public String myTicketsGuestPage(Model model, User user) {

        Response<List<Ticket>> response = dataStore.getTicketsByUserEmail(user.getEmail());
        if (response.isSuccess()) {
            model.addAttribute("tickets", response.getData());
            return "myticketsguest";
        } else {
            model.addAttribute("message", response.getMessage());
            return "error";
        }
    }

}
