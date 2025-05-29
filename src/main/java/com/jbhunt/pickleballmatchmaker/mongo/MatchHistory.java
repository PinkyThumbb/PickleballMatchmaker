package com.jbhunt.pickleballmatchmaker.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "matchHistory")
public class MatchHistory {
    @Id
    private String id;
    private String opponentName;
    private double opponentRating;
    private boolean win;
    private double updatedSkillLevel;
    private LocalDateTime matchDate;
}