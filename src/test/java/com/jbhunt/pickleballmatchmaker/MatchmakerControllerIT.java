package com.jbhunt.pickleballmatchmaker;

import com.jbhunt.pickleballmatchmaker.mongo.PickleballUser;
import com.jbhunt.pickleballmatchmaker.repository.PickleballUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class MatchmakerControllerIT extends BaseIntegrationTest {

    @Autowired
    private PickleballUserRepository repository;

    @BeforeEach
    public void setUp() {
        super.setUp();
        repository.deleteAll();
    }

    @Test
    public void testCreateNewMatchmaker() {
        PickleballUser user = new PickleballUser(null, "DoeHunter", "John Doe", 25, 3.5, 72701);

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/ws_bensprojects_pickleballmatchmaker/createUser", user, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    public void testCreateNewMatchmakerValidationFailure() {
        PickleballUser user = new PickleballUser(null, "DoeHunter", "Ben", 12, null, 72701);

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/ws_bensprojects_pickleballmatchmaker/createUser", user, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(repository.findAll()).isEmpty();
    }
}