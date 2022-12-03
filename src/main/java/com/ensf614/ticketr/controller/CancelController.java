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
import com.ensf614.ticketr.service.EmailService;

@Controller
public class CancelController {

    @Autowired
    DataStore dataStore;

    @Autowired
    EmailService emailService;

    @RequestMapping("/cancel")
    public String showCancelPage(Model model) {
        model.addAttribute("user", new User());
        return "cancel";
    }

    @PostMapping("/cancel")
    public String cancelTicket(User user, Model model) {

        Response<User> userResponse = dataStore.getUserResponseByEmail(user.getEmail());

        if (!userResponse.isSuccess()) {
            model.addAttribute("message", userResponse.getMessage());
            return "error";
        }

        Response<List<Ticket>> response = dataStore.getTicketsByUserEmail(user.getEmail());
        if (response.getData().size() == 0) {
            model.addAttribute("message", "This user has no tickets to cancel.");
            return "error";
        }

        if (response.isSuccess()) {
            model.addAttribute("user", userResponse);
            model.addAttribute("tickets", response.getData());
            return "myticketsguest";
        } else {
            model.addAttribute("message", response.getMessage());
            return "error";
        }
    }

    @PostMapping("/ticket/cancel")
    public String cancelTicketConfirm(int ticketId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Response<Ticket> response = dataStore.getTicket(ticketId);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "error";
        }
        Response<User> userResponse = dataStore.getUser(response.getData().getUserId());

        Response<Boolean> ticketResponse = dataStore.deleteTicket(ticketId);
        if (ticketResponse.getData() == false) {
            return "error";
        }

        emailService.sendCancelEmail(userResponse.getData());

        if (auth.getName().equals("anonymousUser")) {
            model.addAttribute("message",
                    "Your ticket has been cancelled. You will receive credit valid for a year."
                            + "Please note that you have also been charged a 15% admin fee for cancellation."
                            + "If you would like to get a full refund in the future, please register.");

            return "cancelconfirm";
        } else {
            model.addAttribute("message", "Your ticket has been cancelled. You will receive credit valid for a year.");
            return "cancelconfirm";
        }

    }

}