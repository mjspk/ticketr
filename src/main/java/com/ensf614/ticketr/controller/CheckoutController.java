package com.ensf614.ticketr.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ensf614.ticketr.data.DataStore;
import com.ensf614.ticketr.model.Response;
import com.ensf614.ticketr.model.Seat;
import com.ensf614.ticketr.model.Selection;
import com.ensf614.ticketr.model.Showtime;
import com.ensf614.ticketr.model.Ticket;

@Controller
public class CheckoutController {

    @Autowired
    DataStore dataStore;

    @RequestMapping("/checkout")
    public String showCheckoutPage(HttpSession session, Model model) {
        Selection selection = (Selection) session.getAttribute("selection");
        if (selection == null) {
            return "redirect:/";
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
        return "checkout";

    }

    @PostMapping("/checkout")
    public String checkoutPage(HttpSession session, Model model) {
        Selection selection = (Selection) session.getAttribute("selection");
        if (selection == null) {
            return "redirect:/";
        }
        ArrayList<Ticket> tickets = selection.getSelectedTickets();
        Response<ArrayList<Ticket>> response = dataStore.addTickets(tickets);
        if (response.isSuccess()) {
            selection.setSelectedTickets(response.getData());
            model.addAttribute("selection", selection);
            model.addAttribute("message", response.getMessage());
            session.removeAttribute("selection");
            return "confirmation";
        } else {
            model.addAttribute("message", response.getMessage());
            return "error";
        }
    }

}
