package com.ll.com.music_payments.app.home.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/adm")
public class AdmHomeController {


    @PreAuthorize("hasAuthority('ADMIN')") // ADMIN 권한만 접근 가능
    @GetMapping("")
    public String showIdex() {

        return "redirect:/adm/home/main";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("home/main")
    public String showMain() {

        return "/adm/home/main";
    }
}
