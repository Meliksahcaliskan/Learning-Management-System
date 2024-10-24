package com.lsm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContentController {
    
    @GetMapping("/api/auth/login")
    public String login(){
        return "login";
    }
    
    @GetMapping("/api/auth/signup")
    public String signup(){
        return "signup";
    }

    @GetMapping("api/auth/index")
    public String home(){
        return "index";
    }

}
