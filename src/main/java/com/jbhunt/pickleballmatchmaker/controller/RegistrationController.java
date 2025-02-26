package com.jbhunt.pickleballmatchmaker.controller;

import com.jbhunt.pickleballmatchmaker.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String fullName,
                               @RequestParam int age,
                               @RequestParam String zipCode,
                               @RequestParam double skillRating,
                               @RequestParam String password,
                               Model model) {
        try {
            log.info("Registering user: {}, {}, {}, {}, {}, {}", username, fullName, age, zipCode, skillRating, password);
            userService.saveUser(username, fullName, age, zipCode, skillRating, password);

            if (userService.authenticate(username, password)) {
                return "redirect:/home";
            } else {
                model.addAttribute("error", "Authentication failed after registration.");
                return "register";
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}