package com.jbhunt.pickleballmatchmaker.controller;

import com.jbhunt.pickleballmatchmaker.mongo.PickleballUser;
import com.jbhunt.pickleballmatchmaker.service.MatchmakerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MatchmakerControllerTest {
    @Mock
    private MatchmakerService matchmakerService;

    @InjectMocks
    private MatchmakerController matchmakerController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateNewUser() {
        PickleballUser user = new PickleballUser(null, "DoeHunter", "John Doe", 25, 3.5, 72758,"admin");

        when(matchmakerService.createUser(any(PickleballUser.class))).thenReturn(Map.of("id", "1"));

        ResponseEntity<Map<String, String>> response = matchmakerController.createUser(user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(Map.of("id", "1"));
    }

    @Test
    public void testCreateNewUserFailure() {
        PickleballUser user = new PickleballUser(null, null, "", null, null, 1,"admin");

        when(matchmakerService.createUser(any(PickleballUser.class))).thenThrow(new RuntimeException("Validation failed"));

        ResponseEntity<Map<String, String>> response = matchmakerController.createUser(user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(Map.of("error", "Error saving user to database"));
    }
}