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
import com.ensf614.ticketr.data.IDataStore;
import com.ensf614.ticketr.model.Card;
import com.ensf614.ticketr.model.Response;
import com.ensf614.ticketr.model.Selection;
import com.ensf614.ticketr.model.User;

@Controller
public class PaymentController {
    @Autowired
    IDataStore dataStore;

    @RequestMapping("/changepayment")
    public String changePayment(Model model, Card defaultCard, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Response<User> responseuser = dataStore.getUserByEmail(auth.getName());
        User user = responseuser.getData();
        if (user == null) {
            model.addAttribute("message", "You must be logged in to access this page.");
            return "error";
        } else {
            Response<Card> response = dataStore.getDefaultCard(user.getId());
            if (response.isSuccess()) {
                defaultCard = response.getData();
                session.setAttribute("defaultCard", defaultCard);
                model.addAttribute("defaultCard", defaultCard);
            } else {
                defaultCard.setUserId(user.getId());
                session.setAttribute("defaultCard", defaultCard);
                model.addAttribute("defaultCard", defaultCard);
            }
            model.addAttribute("card", new Card());
            return "changepayment";
        }

    }

    @PostMapping("/changepayment")
    public String changePaymentPost(Model model, Card card, HttpSession session) {
        Card defaultCard = (Card) session.getAttribute("defaultCard");
        if (card.getCardNumber().length() != 16) {
            model.addAttribute("message", "Invalid card number.");
            model.addAttribute("defaultCard", defaultCard);
            model.addAttribute("card", card);
            return "changepayment";
        }
        if (card.getExpiryDate().length() != 5) {
            model.addAttribute("defaultCard", defaultCard);
            model.addAttribute("message", "Invalid expiry date.");
            model.addAttribute("card", card);
            return "changepayment";
        }
        if (card.getCvv().length() != 3) {
            model.addAttribute("defaultCard", defaultCard);
            model.addAttribute("message", "Invalid CVV.");
            model.addAttribute("card", card);
            return "changepayment";
        }

        Response<Card> response = dataStore.updateUserPayment(defaultCard.getUserId(), card);
        if (response.isSuccess()) {
            model.addAttribute("message", "Payment information updated.");
            model.addAttribute("defaultCard", response.getData());
            model.addAttribute("card", new Card());
            return "changepayment";
        } else {
            model.addAttribute("message", response.getMessage());
            return "error";
        }
    }
}
