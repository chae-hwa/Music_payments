package com.ll.com.music_payments.app.home.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/adm")
public class AdmHomeController {

    @GetMapping("")
    public String showIdex() {

        return "redirect:/adm/home/main";
    }

    @GetMapping("home/main")
    public String showMain() {

        return "/adm/home/main";
    }
}
