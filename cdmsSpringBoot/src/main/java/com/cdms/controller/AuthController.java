package com.cdms.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    // For logging
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/google-login")
    public String loadLoginPage() {
        return "login";
    }
}
