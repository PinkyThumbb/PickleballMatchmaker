package com.jbhunt.pickleballmatchmaker.controller;

import com.jbhunt.pickleballmatchmaker.mongo.PickleballUser;
import com.jbhunt.pickleballmatchmaker.service.MatchmakerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
public class MatchmakerController {
    private final MatchmakerService matchmakerService;

    @PostMapping("/matchmaker")
    public ResponseEntity<Map<String,String>> createNewMatchmaker(@RequestBody @Valid PickleballUser pickleballUser) {
        try {
            return new ResponseEntity<>(matchmakerService.createNewMatchmaker(pickleballUser), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Unexpected error", e);
            return new ResponseEntity<>(Map.of("error","Error saving user to database"), HttpStatus.BAD_REQUEST);
        }
    }
}