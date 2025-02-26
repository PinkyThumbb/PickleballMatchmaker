package com.jbhunt.pickleballmatchmaker;

import com.jbhunt.pickleballmatchmaker.mongo.PickleballUser;
import com.jbhunt.pickleballmatchmaker.repository.PickleballUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;

public class MatchmakerControllerIT extends BaseIntegrationTest {

    @Autowired
    private PickleballUserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        super.setUp();
        repository.deleteAll();
    }

    @Test
    public void testSearchPlayersByUserName() {
        // Create and save a user
        PickleballUser user = new PickleballUser(null, "DoeHunter", "John Doe", 25, 3.5, 72701, "admin");
        user.setPassword(passwordEncoder.encode("password"));
        repository.save(user);

        // Log in
        MultiValueMap<String, String> loginParams = new LinkedMultiValueMap<>();
        loginParams.add("username", "admin");
        loginParams.add("password", "admin");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> loginRequest = new HttpEntity<>(loginParams, headers);
        ResponseEntity<String> loginResponse = restTemplate.postForEntity("http://localhost:" + port + "/ws_bensprojects_pickleballmatchmaker/login", loginRequest, String.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.FOUND);

        // Check if the login response contains the Set-Cookie header
        String cookies = loginResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertThat(cookies).isNotNull();

        headers.add(HttpHeaders.COOKIE, cookies);

        // Navigate to search page
        HttpEntity<Void> searchPageRequest = new HttpEntity<>(headers);
        ResponseEntity<String> searchPageResponse = restTemplate.exchange("http://localhost:" + port + "/ws_bensprojects_pickleballmatchmaker/userNameSearch", HttpMethod.GET, searchPageRequest, String.class);
        assertThat(searchPageResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Perform search by username
        MultiValueMap<String, String> searchParams = new LinkedMultiValueMap<>();
        searchParams.add("userName", "DoeHunter");

        HttpEntity<MultiValueMap<String, String>> searchRequest = new HttpEntity<>(searchParams, headers);
        ResponseEntity<String> searchResponse = restTemplate.postForEntity("http://localhost:" + port + "/ws_bensprojects_pickleballmatchmaker/searchPlayersByUserName", searchRequest, String.class);

        // Verify the response
        assertThat(searchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(searchResponse.getBody()).contains("DoeHunter", "John Doe", "25", "3.5", "72701");
    }
}