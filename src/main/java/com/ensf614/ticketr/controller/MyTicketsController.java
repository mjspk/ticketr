package com.ensf614.ticketr.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
public class MyTicketsController {

    @RequestMapping("/mytickets")
    public String myTicketsPage(Model model) {
        List<String> tickets = new ArrayList<String>();
        tickets.add("Ticket 1");
        tickets.add("Ticket 2");
        tickets.add("Ticket 3");
        model.addAttribute("tickets", tickets);
        return "mytickets";
    }

    

    
}
