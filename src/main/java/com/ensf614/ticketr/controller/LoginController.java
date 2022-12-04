package com.ensf614.ticketr.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ensf614.ticketr.data.DataStore;
import com.ensf614.ticketr.data.IDataStore;
import com.ensf614.ticketr.model.Response;
import com.ensf614.ticketr.model.Selection;
import com.ensf614.ticketr.model.User;
import com.ensf614.ticketr.securingweb.CustomAuthenticationProvider;

@Controller
public class LoginController {

    @Autowired
    IDataStore dataStore;

    @RequestMapping("/login")
    public String login(HttpServletRequest request,Model model) {
        String referrer = request.getHeader("Referer");
        request.getSession().setAttribute("url_prior_login", referrer);
        model.addAttribute("user", new User());
        return "login";
    }

    // maping for login error
    @RequestMapping("/login?error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login";
    }

    // maping for logout
    @RequestMapping("/logout")
    public String logout(Model model) {
        
        return "login";
    }

   
   

}