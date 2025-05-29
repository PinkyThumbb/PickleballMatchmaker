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
        return EloRating(currentRating, opponentRating, win ? 1.0 : 0.0);
    }

    // Function to calculate the Probability
    public static double Probability(double rating1, double rating2) {
        // Calculate and return the expected score
        return 1.0 / (1 + Math.pow(10, (rating1 - rating2) / 10.0)); // Adjusted for 0-8 scale
    }

    // Function to calculate Elo rating
    // K is a constant.
    // outcome determines the outcome: 1 for Player A win, 0 for Player B win, 0.5 for draw.
    public static double EloRating(double Ra, double Rb, double outcome) {
        final int K = 1; // Base adjustment factor
        final double FLOOR_ADJUSTMENT = 0.01; // Minimum Elo adjustment
        final double MAX_RATING = 8.0;
        final double MIN_RATING = 0.0;

        // Calculate the Winning Probability of Player B
        double Pb = Probability(Ra, Rb);

        // Calculate the Winning Probability of Player A
        double Pa = Probability(Rb, Ra);

        // Calculate Elo change with refined scaling factor
        double ratingDifference = Math.abs(Ra - Rb);
        double scalingFactor = ratingDifference > 1 ? 1 + (ratingDifference / 12) : 0.2; // Dampen close matchups
        double eloChange = K * scalingFactor * (outcome - Pa);

        // Ensure Elo decreases for losses
        if (outcome == 0.0 && eloChange > 0) {
            eloChange = -Math.abs(eloChange);
        }

        // Apply floor adjustment
        if (Math.abs(eloChange) < FLOOR_ADJUSTMENT) {
            eloChange = Math.signum(eloChange) * FLOOR_ADJUSTMENT;
        }

        // Update the Elo Ratings
        Ra = Ra + eloChange;

        // Clamp the rating to the valid range
        Ra = Math.max(MIN_RATING, Math.min(MAX_RATING, Ra));

        // Print updated ratings
        System.out.println("Updated Ratings:-");
        System.out.println("Ra = " + Ra + " Rb = " + Rb);
        return Ra;
    }
}