package com.ensf614.ticketr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ensf614.ticketr.data.DataStore;
import com.ensf614.ticketr.model.Response;
import com.ensf614.ticketr.model.Selection;
import com.ensf614.ticketr.model.User;

@Controller
public class RegisterController {

    @Autowired
    DataStore dataStore;

    @RequestMapping("/register")

    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(User user, Model model) {

        Response<User> response = dataStore.registerUser(user);
        if (response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "login";
        } else {
            model.addAttribute("message", response.getMessage());
            return "register";
        }
    }

    @PostMapping("/saveuser")
    public String saverUser(Model model, Selection selection) {
        Response<User> response = dataStore.addUser(selection.getUser());
        if (response.isSuccess()) {
            selection.setUser(response.getData());
            model.addAttribute("selection", selection);
            return "checkout";
        } else {
            model.addAttribute("message", response.getMessage());
            return "userinfo";
        }
    }

}