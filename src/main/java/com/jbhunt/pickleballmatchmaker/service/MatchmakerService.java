package com.jbhunt.pickleballmatchmaker.service;

import com.jbhunt.pickleballmatchmaker.mongo.PickleballUser;
import com.jbhunt.pickleballmatchmaker.repository.PickleballUserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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

    //make new method to query current skill rating and post new one after match ends
    public void reportScore(Integer opponent, boolean win) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("Auth name: " + auth.getName());
        List<PickleballUser> player = pickleballUserRepository.findByUserName(auth.getName());


        PickleballUser player1 = player.get(0);
        double updatedSkillLevel = updatePlayerRating(player1.getSkillLevel(), opponent, win);
        player1.setSkillLevel(Math.round(updatedSkillLevel * 100.0) / 100.0);
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