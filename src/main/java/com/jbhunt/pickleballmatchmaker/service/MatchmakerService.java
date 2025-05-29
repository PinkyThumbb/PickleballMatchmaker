package com.jbhunt.pickleballmatchmaker.service;

import com.jbhunt.pickleballmatchmaker.mongo.MatchHistory;
import com.jbhunt.pickleballmatchmaker.mongo.PickleballUser;
import com.jbhunt.pickleballmatchmaker.repository.PickleballUserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class MatchmakerService {
    private final PickleballUserRepository pickleballUserRepository;

    public Map<String, String> createUser(PickleballUser pickleballUser) {
        try {
            Map<String, String> response = new HashMap<>();
            response.put("id", pickleballUserRepository.save(pickleballUser).getId());
            return response;
        } catch (DuplicateKeyException e) {
            log.error("Duplicate username", e);
            throw new DuplicateKeyException("Username already exists");
        } catch (Exception e) {
            log.error("Error saving user", e);
            throw e;
        }
    }

    public List<PickleballUser> findPlayersByZipCode(int zipCode) {
        return pickleballUserRepository.findByZipCode(zipCode);
    }

    public List<PickleballUser> findPlayersByUserName(String userName) {
        return pickleballUserRepository.findByUserName(userName);
    }

    public List<PickleballUser> findPlayersBySkillLevelRange(double skillLevelLower, double skillLevelUpper) {
        return pickleballUserRepository.findBySkillLevelBetween(skillLevelLower, skillLevelUpper);
    }

    public List<PickleballUser> findAllUsers() {
        return pickleballUserRepository.findAll();
    }

    public void reportScore(Double opponentRating, boolean win) {
        // Retrieve the logged-in user
        var auth = SecurityContextHolder.getContext().getAuthentication();
        List<PickleballUser> player = pickleballUserRepository.findByUserName(auth.getName());

        PickleballUser player1 = player.get(0);

        // Update the player's skill level
        double updatedSkillLevel = updatePlayerRating(player1.getSkillLevel(), opponentRating, win);
        player1.setSkillLevel(Math.round(updatedSkillLevel * 100.0) / 100.0);

        // Create a new match history entry
        MatchHistory matchHistory = new MatchHistory();
        matchHistory.setOpponentName("Opponent"); // Replace with actual opponent name if available
        matchHistory.setOpponentRating(opponentRating);
        matchHistory.setWin(win);
        matchHistory.setUpdatedSkillLevel(player1.getSkillLevel());
        matchHistory.setMatchDate(LocalDateTime.now());

        // Add the match history entry to the user's match history
        if (player1.getMatchHistory() == null) {
            player1.setMatchHistory(new ArrayList<>());
        }
        player1.getMatchHistory().add(matchHistory);

        // Save the updated user back to the database
        pickleballUserRepository.save(player1);
    }

    public double updatePlayerRating(double currentRating, double opponentRating, boolean win) {
        // Constants
        final double K = 0.2;
        final double MIN_RATING = 0.0;
        final double MAX_RATING = 6.0;

        // Calculate expected score
        double expectedScore = 1 / (1 + Math.pow(10, (opponentRating - currentRating) / 400));

        // Determine actual score
        double actualScore = win ? 1.0 : 0.0;

        // Update rating
        double newRating = currentRating + K * (actualScore - expectedScore);

        // Clamp the rating to the valid range
        newRating = Math.max(MIN_RATING, Math.min(MAX_RATING, newRating));

        return newRating;
    }
}