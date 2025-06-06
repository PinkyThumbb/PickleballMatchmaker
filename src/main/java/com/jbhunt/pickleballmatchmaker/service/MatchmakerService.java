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
import java.util.stream.Collectors;

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

    public Map<String, Object> getPaginatedMatchHistory(String playerUserName, int page, int pageSize) {
        List<PickleballUser> player = pickleballUserRepository.findByUserName(playerUserName);
        if (player.isEmpty()) {
            throw new IllegalArgumentException("Player not found");
        }
        List<MatchHistory> matchHistory = player.get(0).getMatchHistory();
        if (matchHistory == null || matchHistory.isEmpty()) {
            return Map.of("paginatedHistory", new ArrayList<>(), "totalPages", 1);
        }

        // Sort match history by date in descending order
        matchHistory.sort((m1, m2) -> m2.getMatchDate().compareTo(m1.getMatchDate()));

        // Calculate pagination details
        int totalPages = (int) Math.ceil((double) matchHistory.size() / pageSize);
        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, matchHistory.size());
        List<MatchHistory> paginatedHistory = matchHistory.subList(startIndex, endIndex);

        // Return paginated results and total pages
        Map<String, Object> result = new HashMap<>();
        result.put("paginatedHistory", paginatedHistory);
        result.put("totalPages", totalPages);
        return result;
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

    public void addFriend(String userName, String friendUserName) {
        if(userName.equalsIgnoreCase(friendUserName)) {
            throw new IllegalArgumentException("Cannot add yourself as a friend");
        }

        List<PickleballUser> user = pickleballUserRepository.findByUserName(userName);
        List<PickleballUser> friend = pickleballUserRepository.findByUserName(friendUserName);

        if (user.isEmpty() || friend.isEmpty()) {
            throw new IllegalArgumentException("User or friend not found");
        }

        PickleballUser userEntity = user.get(0);
        PickleballUser friendEntity = friend.get(0);
        if( userEntity.getFriends() == null ) {
            userEntity.setFriends(new ArrayList<>());
        }
        if (!userEntity.getFriends().contains(friendEntity.getUserName())) {
            userEntity.getFriends().add(friendEntity);
            pickleballUserRepository.save(userEntity);
        }
        else{
            throw new IllegalArgumentException("Friend already exists in the user's friend list");
        }
    }

    public void removeFriend(String userName, String friendUserName) {
        List<PickleballUser> user = pickleballUserRepository.findByUserName(userName);

        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        PickleballUser userEntity = user.get(0);

        if (userEntity.getFriends() != null && userEntity.getFriends().stream().anyMatch(friend -> friend.getUserName().equalsIgnoreCase(friendUserName))) {
            userEntity.setFriends(
                    userEntity.getFriends().stream()
                            .filter(friend -> !friend.getUserName().equalsIgnoreCase(friendUserName))
                            .collect(Collectors.toList())
            );
            pickleballUserRepository.save(userEntity);
        } else {
            throw new IllegalArgumentException("Friend not found in user's friend list");
        }
    }

    public List<Map<String, Object>> viewFriends(String userName) {
        List<PickleballUser> user = pickleballUserRepository.findByUserName(userName);

        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        PickleballUser userEntity = user.get(0);

        if (userEntity.getFriends() == null || userEntity.getFriends().isEmpty()) {
            throw new IllegalArgumentException("No friends found");
        }

        return userEntity.getFriends().stream()
                .map(friend -> Map.of(
                        "userName", friend.getUserName(),
                        "name", friend.getName(),
                        "skillLevel", (Object) friend.getSkillLevel()
                ))
                .collect(Collectors.toList());
    }
}