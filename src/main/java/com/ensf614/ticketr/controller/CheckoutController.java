package com.ensf614.ticketr.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String checkoutPage(Model model, Selection selection) {
        ArrayList<Ticket> tickets = new ArrayList<>();
        for (String seatId : selection.getSelectedSeats()) {
            Ticket ticket = new Ticket();
            ticket.setSeat(new Seat() {
                {
                    setId(Integer.parseInt(seatId));
                }
            });
            ticket.setShowtime(new Showtime() {
                {
                    setId(selection.getSelectedShowtimeId());
                }
            });
            ticket.setUserId(selection.getUser().getId());
            ticket.setPrice(10.00);
            ticket.setStatus("purchased");
            tickets.add(ticket);
        }
        Response<List<Ticket>> response = dataStore.addTickets(tickets);

        if (response.isSuccess()) {
            model.addAttribute("tickets", response.getData());
            model.addAttribute("message", response.getMessage());
            return "confirmation";
        } else {
            model.addAttribute("message", response.getMessage());
            return "error";
        }
    }

}
