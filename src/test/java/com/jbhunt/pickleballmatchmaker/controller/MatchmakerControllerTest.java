package com.jbhunt.pickleballmatchmaker.controller;

import com.jbhunt.pickleballmatchmaker.mongo.PickleballUser;
import com.jbhunt.pickleballmatchmaker.repository.PickleballUserRepository;
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
    public void testCreateNewMatchmaker() {
        PickleballUser user = new PickleballUser(null, "DoeHunter", "John Doe", 25, 3.5, 72758);

        when(matchmakerService.createNewMatchmaker(any(PickleballUser.class))).thenReturn(Map.of("id", "1"));

        ResponseEntity<Map<String, String>> response = matchmakerController.createNewMatchmaker(user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(Map.of("id", "1"));
    }

    @Test
    public void testCreateNewMatchmakerValidationFailure() {
        PickleballUser user = new PickleballUser(null, null, "", null, null, 1);

        when(matchmakerService.createNewMatchmaker(any(PickleballUser.class))).thenThrow(new RuntimeException("Validation failed"));

        ResponseEntity<Map<String, String>> response = matchmakerController.createNewMatchmaker(user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(Map.of("error", "Error saving user to database"));
    }
}