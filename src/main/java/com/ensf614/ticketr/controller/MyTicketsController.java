package com.ensf614.ticketr.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ensf614.ticketr.data.DataStore;
import com.ensf614.ticketr.model.Response;
import com.ensf614.ticketr.model.Ticket;
import com.ensf614.ticketr.service.EmailService;

@Controller
public class MyTicketsController {
    @Autowired
    DataStore dataStore;

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

    
}
