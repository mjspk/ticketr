package com.ensf614.ticketr.controller;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ensf614.ticketr.data.DataStore;
import com.ensf614.ticketr.model.*;
import com.ensf614.ticketr.model.Response;

@Controller
public class MyNewsController {

    @Autowired
    DataStore dataStore;

    @RequestMapping("/mynews")
    public String myNewsPage(Model model) {
        Response<ArrayList<News>> response = dataStore.getMoviesNews();
        if (response.isSuccess()) {
            model.addAttribute("news", response.getData());
            return "mynews";
        } else {
            model.addAttribute("message", response.getMessage());
            return "error";
        }
    }
}
