package com.jbhunt.pickleballmatchmaker.service;

import com.jbhunt.pickleballmatchmaker.mongo.PickleballUser;
import com.jbhunt.pickleballmatchmaker.repository.PickleballUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MatchmakerServiceTest {

    @Mock
    private PickleballUserRepository repository;

    @InjectMocks
    private MatchmakerService matchmakerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateNewMatchmaker() {
        PickleballUser user = new PickleballUser(null,"DoeHunter", "John Doe", 25, 3.5,1);
        PickleballUser savedUser = new PickleballUser("1","DoeHunter", "John Doe", 25, 3.5,1);

        when(repository.save(any(PickleballUser.class))).thenReturn(savedUser);

        Map<String, String> result = matchmakerService.createUser(user);

        assertThat(result).isEqualTo(Map.of("id", "1"));
    }
}