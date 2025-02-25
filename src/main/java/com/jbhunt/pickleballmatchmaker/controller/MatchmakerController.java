package com.jbhunt.pickleballmatchmaker.controller;

import com.jbhunt.pickleballmatchmaker.mongo.PickleballUser;
import com.jbhunt.pickleballmatchmaker.service.MatchmakerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
        } catch (DuplicateKeyException e) {
            log.error("Unique username conflict", e);
            return new ResponseEntity<>(Map.of("error", "Username is not unique!"), HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Unexpected error", e);
            return new ResponseEntity<>(Map.of("error","Error saving user to database"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/findPlayersByZipCode")
    public ResponseEntity<List<PickleballUser>> findPlayersByZipCode(@RequestParam("zipCode") int zipCode) {
        try {
            return new ResponseEntity<>(matchmakerService.findPlayersByZipCode(zipCode), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Unexpected error", e);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/findPlayersByUserName")
    public ResponseEntity<List<PickleballUser>> findPlayersByUserName(@RequestParam("userName") String userName) {
        try {
            return new ResponseEntity<>(matchmakerService.findPlayersByUserName(userName), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Unexpected error", e);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}