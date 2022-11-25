package com.ensf614.ticketr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MyOffersController {

    @RequestMapping("/myoffers")
    public String myOffersPage() {
        return "myoffers";
    }
    
}
