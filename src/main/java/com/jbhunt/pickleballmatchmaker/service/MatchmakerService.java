package com.jbhunt.pickleballmatchmaker.service;

import com.jbhunt.pickleballmatchmaker.mongo.PickleballUser;
import com.jbhunt.pickleballmatchmaker.repository.PickleballUserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class MatchmakerService {
    private final PickleballUserRepository pickleballUserRepository;

    public Map<String, String> createNewMatchmaker(PickleballUser pickleballUser) {
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
}