package com.ensf614.ticketr.controller;

import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ensf614.ticketr.data.DataStore;
import com.ensf614.ticketr.data.IDataStore;
import com.ensf614.ticketr.model.Response;
import com.ensf614.ticketr.model.Role;
import com.ensf614.ticketr.model.Selection;
import com.ensf614.ticketr.model.User;

@Controller
public class RegisterController {

    @Autowired
    IDataStore dataStore;

    @RequestMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @RequestMapping("/registeration_payment")
    public String paymentPage(User user, HttpSession session, Model model) {
        if (user.getFirstName().isEmpty() || user.getLastName().isEmpty() || user.getEmail().isEmpty()
                || user.getPassword().isEmpty() || user.getPhone().isEmpty()) {
            model.addAttribute("message", "Please fill out all fields");
            return "register";
        }
        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            model.addAttribute("message", "Please enter a valid email");
            return "register";
        }
        if (dataStore.isEmailInUse(user.getEmail())) {
            model.addAttribute("message", "Email already in use");
            return "register";
        }
        if (user.getPassword().length() < 8) {
            model.addAttribute("message",
                    "Password must be at least 8 characters, contain at least one uppercase letter, one lowercase letter, one number, and one special character");
            return "register";
        }
        session.setAttribute("user", user);
        model.addAttribute("user", user);
        return "registeration_payment";
    }

    @PostMapping("/register")
    public String registerUser(User myuser, HttpSession session, Model model) {

        if (myuser.getDefaultCard().getCardNumber().length() != 16) {
            model.addAttribute("message", "Please enter a valid credit card number");
            return "registeration_payment";
        }
        if (myuser.getDefaultCard().getExpiryDate().length() != 5) {
            model.addAttribute("message", "Please enter a valid credit card expiration date");
            return "registeration_payment";
        }
        if (myuser.getDefaultCard().getCvv().length() != 3) {
            model.addAttribute("message", "Please enter a valid credit card cvv");
            return "registeration_payment";
        }
        if (myuser.getDefaultCard().getCardHolderName().isEmpty()) {
            model.addAttribute("message", "Please enter a valid credit card name");
            return "registeration_payment";
        }

        User user = (User) session.getAttribute("user");
        user.setDefaultCard(myuser.getDefaultCard());
        user.setRolStrings(new String[] { "ROLE_USER" });
        Response<User> response = dataStore.registerUser(user);
        if (response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "login";
        } else {
            model.addAttribute("message", response.getMessage());
            return "payment";
        }
    }

    @PostMapping("/saveuser")
    public String saverUser(Model model, HttpSession session, Selection myselection) {
        Selection selection = (Selection) session.getAttribute("selection");
        if (selection == null) {
            return "redirect:/";
        }
        User user = myselection.getUser();

        if (user.getFirstName().isEmpty() || user.getLastName().isEmpty() || user.getEmail().isEmpty()
                || user.getPhone().isEmpty()) {
            model.addAttribute("message", "Please fill out all fields");
            model.addAttribute("selection", selection);
            session.setAttribute("selection", selection);
            return "userinfo";
        }

        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            model.addAttribute("message", "Please enter a valid email");
            model.addAttribute("selection", selection);
            session.setAttribute("selection", selection);
            return "userinfo";
        }

        if (dataStore.isEmailInUse(user.getEmail())) {
            model.addAttribute("message", "Email already in use");
            model.addAttribute("selection", selection);
            session.setAttribute("selection", selection);
            return "userinfo";
        }

        Response<User> response = dataStore.addUser(user);
        if (response.isSuccess()) {
            selection.setUser(response.getData());
            session.setAttribute("selection", selection);
            return "redirect:/checkout";
        } else {
            model.addAttribute("message", response.getMessage());
            return "userinfo";
        }
    }

}