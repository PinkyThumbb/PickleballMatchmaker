package com.jbhunt.pickleballmatchmaker.controller;

import com.jbhunt.pickleballmatchmaker.mongo.PickleballUser;
import com.jbhunt.pickleballmatchmaker.service.MatchmakerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @GetMapping("/reportGameScore")
    public String reportGameScore(@RequestParam Double opponentRating, @RequestParam boolean win, Model model) {
        try {
            matchmakerService.reportScore(opponentRating, win);
            var auth = SecurityContextHolder.getContext().getAuthentication();
            List<PickleballUser> player = matchmakerService.findPlayersByUserName(auth.getName());
            model.addAttribute("success", "Game score reported successfully!");
            model.addAttribute("newSkillLevel", player.get(0).getSkillLevel());
        } catch (Exception e) {
            model.addAttribute("error", "Failed to report game score.");
        }
        return "reportScore";
    }

    @GetMapping("/viewAllPlayers")
    public String viewAllUsers(Model model) {
        try {
            List<PickleballUser> players = matchmakerService.findAllUsers();
            model.addAttribute("players", players);
            return "fullPlayerList";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to retrieve users.");
            return "fullPlayerList";
        }
    }

    @GetMapping("/viewPlayerMatchHistory")
    public String viewPlayerMatchHistory(@RequestParam(value = "playerId", required = false) String playerUserName, @RequestParam(defaultValue = "0") int page, Model model) {
        try {
            if (playerUserName == null || playerUserName.isEmpty()) {
                var auth = SecurityContextHolder.getContext().getAuthentication();
                List<PickleballUser> loggedInUser = matchmakerService.findPlayersByUserName(auth.getName());
                playerUserName = loggedInUser.get(0).getUserName();
            }

            int pageSize = 10;
            Map<String, Object> paginatedData = matchmakerService.getPaginatedMatchHistory(playerUserName, page, pageSize);

            model.addAttribute("matchHistory", paginatedData.get("paginatedHistory"));
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", paginatedData.get("totalPages"));
            model.addAttribute("playerName", playerUserName);
            return "matchHistory";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to retrieve match history for the player.");
            return "matchHistory";
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
    @GetMapping("/addFriendForm")
    public String addFriendForm(Model model) {
        return "addFriend";
    }
    @PostMapping("/addFriend")
    public ResponseEntity<String> addFriend(@RequestParam String friendUserName) {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            matchmakerService.addFriend(auth.getName(), friendUserName);
            return ResponseEntity.ok("Friend added successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to add friend");
        }
    }

    @DeleteMapping("/removeFriend")
    public ResponseEntity<String> removeFriend(@RequestParam String friendUserName) {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            matchmakerService.removeFriend(auth.getName(), friendUserName);
            return ResponseEntity.ok("Friend removed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to remove friend");
        }
    }

    @GetMapping("/viewFriends")
    public String viewFriends(Model model) {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            List<Map<String, Object>> friends = matchmakerService.viewFriends(auth.getName());
            model.addAttribute("friends", friends);
            return "friendsList";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to retrieve friends list.");
            return "friendsList";
        }
    }
}