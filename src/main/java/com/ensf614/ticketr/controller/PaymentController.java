package com.ensf614.ticketr.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ensf614.ticketr.data.DataStore;
import com.ensf614.ticketr.model.Card;
import com.ensf614.ticketr.model.Response;
import com.ensf614.ticketr.model.Selection;
import com.ensf614.ticketr.model.User;

@Controller
public class PaymentController {
    @Autowired
    DataStore dataStore;

    @RequestMapping("/changepayment")
    public String changePayment(Model model, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = dataStore.getUserByEmail(auth.getName());
        Card defaultcard = dataStore.getDefaultCard(user.getId());
        user.setDefaultCard(defaultcard);
        if (user == null) {
            model.addAttribute("message", "You must be logged in to access this page.");
            return "error";
        } else {
            model.addAttribute("user", user);
            Card card =  new Card();
            card.setUserId(user.getId());
            model.addAttribute("card", card);
            return "changepayment";
        }

    }

    @PostMapping("/changepayment")
    public String changePaymentPost(Model model, HttpSession session, User user, Card card) {
        if (card.getCardNumber().length() != 16) {
            model.addAttribute("message", "Invalid card number.");
            return "changepayment";
        }
        if (card.getExpiryDate().length() != 4) {
            model.addAttribute("message", "Invalid card expiry.");
            return "changepayment";
        }
        if (card.getCvv().length() != 3) {
            model.addAttribute("message", "Invalid card cvv.");
            return "changepayment";
        }

        Response<User> response = dataStore.updateUserPayment(user, card);
        if (response.isSuccess()) {
            model.addAttribute("user", response.getData());
            model.addAttribute("card", new Card());
            return "changepayment";
        } else {
            model.addAttribute("message", response.getMessage());
            return "error";
        }
    }
}
