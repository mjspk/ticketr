package com.ensf614.ticketr.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ensf614.ticketr.data.DataStore;
import com.ensf614.ticketr.model.Response;
import com.ensf614.ticketr.model.Ticket;
import com.ensf614.ticketr.model.User;

@Controller
public class CancelController {

    @Autowired
    DataStore dataStore;

    @RequestMapping("/cancel")
    public String showCancelPage(Model model) {
        model.addAttribute("user", new User());
        return "cancel";
    }

    @PostMapping("/cancel")
    public String cancelTicket(User user, Model model) {
        System.out.println(user.getEmail());
        Response<List<Ticket>> response = dataStore.getTicketsByUserEmail(user.getEmail());
        if (response.isSuccess()) {
            model.addAttribute("message", user);
            return "myticketsguest";
        } else {
            model.addAttribute("message", response.getMessage());
            return "cancel";
        }
    }

    @PostMapping("/ticket/cancel")
    public String cancelTicketConfirm(int ticketId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.isAuthenticated()) {
            model.addAttribute("message", "Your ticket has been cancelled at no charge.");
            return "cancelconfirm";
        } else {
            model.addAttribute("message",
                    "Your ticket has been cancelled. You will receive credit valid for a year. \n Please note that you have also been charged a 15% admin fee for cancellation.");
            return "cancelconfirm";
        }

    }

}