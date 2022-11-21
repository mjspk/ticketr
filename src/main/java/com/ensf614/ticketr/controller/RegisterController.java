package com.ensf614.ticketr.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ensf614.ticketr.data.DataStore;

@Controller
public class RegisterController {

    @Autowired
    DataStore dataStore;

    @RequestMapping("/register")

    public String registerPage(Model model) {

        return "register";

    }

}