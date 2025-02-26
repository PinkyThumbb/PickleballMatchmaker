package com.jbhunt.pickleballmatchmaker.controller;

import com.jbhunt.pickleballmatchmaker.mongo.PickleballUser;
import com.jbhunt.pickleballmatchmaker.service.MatchmakerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@AllArgsConstructor
public class MatchmakerController {
    private final MatchmakerService matchmakerService;

    @PostMapping("/createUser")
    public ResponseEntity<Map<String,String>> createUser(@RequestBody @Valid PickleballUser pickleballUser) {
        try {
            return new ResponseEntity<>(matchmakerService.createUser(pickleballUser), HttpStatus.CREATED);
        } catch (DuplicateKeyException e) {
            log.error("Unique username conflict", e);
            return new ResponseEntity<>(Map.of("error", "Username is not unique!"), HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Unexpected error", e);
            return new ResponseEntity<>(Map.of("error","Error saving user to database"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/searchPlayersByZipCode")
    public String findPlayersByZipCode(@RequestParam("zipCode") String zipCode, Model model) {
        try {
            int zip = Integer.parseInt(zipCode);
            List<PickleballUser> players = matchmakerService.findPlayersByZipCode(zip);
            model.addAttribute("players", players);
            return "zipCodeSearch";
        } catch (NumberFormatException e) {
            model.addAttribute("error", "Invalid zip code format");
            return "zipCodeSearch";
        }
    }

    @GetMapping("/findPlayersByUserName")
    public String findPlayersByUserName(@RequestParam("userName") String userName, Model model) {
        try {
            List<PickleballUser> players = matchmakerService.findPlayersByUserName(userName);
            model.addAttribute("players", players);
            return "userNameSearch";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while searching for players.");
            return "userNameSearch";
        }
    }

    @GetMapping("/findPlayersBySkillLevel")
    public String findPlayersBySkillLevel(@RequestParam("skillLevelLower") double skillLevelLower, @RequestParam("skillLevelUpper") double skillLevelUpper, Model model) {
        try {
            List<PickleballUser> players = matchmakerService.findPlayersBySkillLevelRange(skillLevelLower, skillLevelUpper);
            model.addAttribute("players", players);
            return "skillLevelSearch";
        } catch (Exception e) {
            model.addAttribute("error", "Random error");
            return "skillLevelSearch";
        }
    }
}