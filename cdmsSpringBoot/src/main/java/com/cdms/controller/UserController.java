package com.cdms.controller;

import com.cdms.dao.UserRepository;
import com.cdms.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class UserController {

    // For logging
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserRepository userRepository;

    @GetMapping("/home")
    public String home(HttpServletRequest request,HttpServletResponse response){
        System.out.println("==Before Context");
        String contextValue = (String) request.getSession().getAttribute("SPRING_SECURITY_CONTEXT").toString();
        System.out.println("==After context : "+contextValue);
        String userId = contextValue.split("sub=")[1].split(", email_verified")[0];
        System.out.println("==USER ID : "+userId);
        //create a cookie
        Cookie cookie = new Cookie("user_id", userId);
        //add cookie to response
        response.addCookie(cookie);
        return "redirect:/";
    }


    @GetMapping(value = "/")
    public String index(@CookieValue(value = "user_id", defaultValue = "noname") String userId, Model model) {
        User user = userRepository.findByUserId(userId);
        System.out.println("=====================");
        System.out.println(user.getEmail());
        System.out.println(user.getName());
        System.out.println(user.getImageUrl());
        System.out.println("=====================");
        model.addAttribute("email", user.getEmail());
        model.addAttribute("user_name", user.getName());
        model.addAttribute("user_notification_status", user.isNotificationEnabled());
        model.addAttribute("user_profile", (user.getImageUrl()==null|| user.getImageUrl().equals("https://bootdey.com/img/Content/avatar/avatar6.png"))?"": user.getImageUrl());
        model.addAttribute("user", user);
        return "home";
    }


    @RequestMapping(value = "/logout")
    public String logout(HttpServletRequest request) {
        log.info("LoginAndRegister : In logout");
        request.getSession().invalidate();
        log.info("LoginAndRegister : User logeout successfully , Redirect to login page");
        return "redirect:/";
    }

    // Update phone number
    @PostMapping("/notificationStatusUpdate")
    public String notificationStatusUpdate(@RequestParam boolean status, @CookieValue(value = "user_id", defaultValue = "noname") String userId) {
        log.info("UserDetailUpdateController : In notificationStatusUpdate");
        User user = userRepository.findByUserId(userId);
        log.info("UserDetailUpdateController : Setting notification Status");
        user.setNotificationEnabled(status);
        userRepository.save(user);
        log.info("UserDetailUpdateController : Redirecting to homepage");
        return "redirect:/";
    }

}
