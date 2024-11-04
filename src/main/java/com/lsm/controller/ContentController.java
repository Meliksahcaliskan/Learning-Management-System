package com.lsm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContentController {
    
    @GetMapping("/api/v1/auth/login")
    public String login(){
        return "login";
    }
    
    @GetMapping("/api/v1/auth/signup")
    public String signup(){
        return "signup";
    }

    @GetMapping("api/v1/auth/index")
    public String home(){
        return "index";
    }

}
