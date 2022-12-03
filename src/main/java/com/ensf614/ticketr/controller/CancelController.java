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

        Response<User> userResponse = dataStore.getUserByEmail(user.getEmail());

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

        Response<Ticket> response = dataStore.getTicket(ticketId);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "error";
        }
        Response<User> userResponse = dataStore.getUser(response.getData().getUserId());
        if (!userResponse.isSuccess()) {
            model.addAttribute("message", userResponse.getMessage());
            return "error";
        }
        User user = userResponse.getData();

        Response<Boolean> ticketResponse = dataStore.cancelTicket(response.getData());
        if (!ticketResponse.isSuccess()) {
            model.addAttribute("message", ticketResponse.getMessage());
            return "error";
        }
        dataStore.addUserCredit(user.getId(), response.getData().getPrice());
        emailService.sendCancelEmail(user);
        if (user.getRolesString().contains("ROLE_USER")) {
            model.addAttribute("message", "Your ticket has been cancelled. You will receive credit valid for a year.");
            return "cancelconfirm";
        } else {
            model.addAttribute("message",
                    "Your ticket has been cancelled. You will receive credit valid for a year."
                            + "Please note that you have also been charged a 15% admin fee for cancellation."
                            + "If you would like to get a full refund in the future, please register.");

            return "cancelconfirm";

        }

    }

}