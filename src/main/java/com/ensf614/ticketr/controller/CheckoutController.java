package com.ensf614.ticketr.controller;

import java.util.ArrayList;
import java.util.List;

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
import com.ensf614.ticketr.model.Receipt;
import com.ensf614.ticketr.model.Response;
import com.ensf614.ticketr.model.Seat;
import com.ensf614.ticketr.model.Selection;
import com.ensf614.ticketr.model.Ticket;
import com.ensf614.ticketr.model.User;
import com.ensf614.ticketr.service.EmailService;

@Controller
public class CheckoutController {

    @Autowired
    IDataStore dataStore;

    @RequestMapping("/checkout")
    public String showCheckoutPage(HttpSession session, Model model) {
        Selection selection = (Selection) session.getAttribute("selection");
        if (selection == null) {
            return "redirect:/";
        }

        if (selection.getUser() == null || selection.getUser().getEmail() == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth.getName() != "anonymousUser") {
                Response<User> responseuser = dataStore.getUserByEmail(auth.getName());
                if (responseuser.isSuccess()) {
                    User user = responseuser.getData();
                    Response<Card> response = dataStore.getDefaultCard(user.getId());
                    if (response.isSuccess()) {
                        selection.setCard(response.getData());
                        selection.setUser(user);

                    } else {
                        model.addAttribute("message",
                                "You must add a payment method to your account before you can purchase tickets.");
                        return "redirect:/changepayment";
                    }
                } else {
                    model.addAttribute("message", "You must be logged in to access this page.");
                    return "error";
                }
            } else {
                session.setAttribute("selection", selection);
                model.addAttribute("selection", selection);
                return "userinfo";
            }
        }
        ArrayList<Ticket> tickets = new ArrayList<Ticket>();
        for (Seat seat : selection.getSelectedSeats()) {
            Ticket ticket = new Ticket();
            ticket.setSeat(seat);
            ticket.setShowtime(selection.getSelectedShowtime());
            ticket.setPrice(10.00);
            ticket.setUserId(selection.getUser().getId());
            ticket.setStatus("PENDING");
            tickets.add(ticket);
        }
        selection.setSelectedTickets(tickets);
        session.setAttribute("selection", selection);
        model.addAttribute("selection", selection);
        if (selection.getCard() != null) {
            model.addAttribute("card", selection.getCard());
        } else {
            model.addAttribute("card", new Card());
        }

        return "checkout";

    }

    @Autowired
    EmailService emailService;

    @PostMapping("/checkout")
    public String checkoutPage(HttpSession session, Model model, Card card) {
        Selection selection = (Selection) session.getAttribute("selection");
        if (selection == null) {
            return "redirect:/";
        }
        if(selection.getCard() == null) {
            session.setAttribute("selection", selection);
            model.addAttribute("selection", selection);
            model.addAttribute("card", card);

            if (card.getCardNumber().length() != 16) {
                model.addAttribute("message", "Invalid card number");
                return "checkout";
            }
            if (card.getExpiryDate().length() != 5) {
                model.addAttribute("message", "Invalid expiration date");
                return "checkout";
            }
            if (card.getCvv().length() != 3) {
                model.addAttribute("message", "Invalid CVV");
                return "checkout";
            }

            selection.setCard(card);
        }

        Response<Receipt> receiptResponse = dataStore.processPayment(selection);
        if (receiptResponse.isSuccess()) {
            Response<ArrayList<Ticket>> ticketResponse = dataStore.addTickets(selection.getSelectedTickets(),
                    selection.getUser().getId());
            if (ticketResponse.isSuccess()) {
                selection.setSelectedTickets(ticketResponse.getData());
                session.setAttribute("selection", selection);
                model.addAttribute("selection", selection);
                emailService.sendHtmlReceipt(receiptResponse.getData());
                return "confirmation";
            } else {
                model.addAttribute("message", ticketResponse.getMessage());
                return "error";
            }

        } else {
            model.addAttribute("message", receiptResponse.getMessage());
            return "error";
        }
    }

}
