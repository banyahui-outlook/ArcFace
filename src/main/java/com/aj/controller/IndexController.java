package com.aj.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
    @Value("${aj.expire}")
    private String expire;

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("expire", expire);
        return "index";
    }
}
