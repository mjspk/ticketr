package com.ensf614.ticketr.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ensf614.ticketr.data.DataStore;
import com.ensf614.ticketr.data.IDataStore;
import com.ensf614.ticketr.model.Offer;
import com.ensf614.ticketr.model.Response;

@Controller
public class MyOffersController {
    @Autowired
    IDataStore dataStore;

    @RequestMapping("/myoffers")
    public String myOffersPage(Model model) {
        Response<ArrayList<Offer>> response = dataStore.getOffers();
        if (response.isSuccess()) {
            model.addAttribute("offers", response.getData());
            return "myoffers";
        } else {
            model.addAttribute("message", response.getMessage());
            return "error";
        }
    }    
}
