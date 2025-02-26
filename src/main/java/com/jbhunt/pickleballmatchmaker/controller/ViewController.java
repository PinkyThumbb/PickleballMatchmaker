package com.jbhunt.pickleballmatchmaker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    @GetMapping("/zipCodeSearch")
    public String showFindPlayersByZipCodePage(Model model) {
        return "zipCodeSearch";
    }

    @GetMapping("/helloWorld")
    public String showHelloWorldPage(Model model) {
        return "helloWorld";
    }

    @GetMapping("/skillLevelSearch")
    public String showFindPlayersBySkillLevelPage(Model model) {
        return "skillLevelSearch";
    }
}