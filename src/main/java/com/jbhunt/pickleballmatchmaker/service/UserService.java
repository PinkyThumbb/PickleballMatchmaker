package com.jbhunt.pickleballmatchmaker.service;

import com.jbhunt.pickleballmatchmaker.mongo.PickleballUser;
import com.jbhunt.pickleballmatchmaker.repository.PickleballUserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private final PickleballUserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void saveUser(String username, String fullName, int age, String zipCode, double skillRating, String password) {
        if (!userRepository.findByUserName(username).isEmpty()) {
            throw new IllegalArgumentException("User already exists");
        }
        PickleballUser user = new PickleballUser();
        user.setUserName(username);
        user.setName(fullName);
        user.setAge(age);
        user.setZipCode(Integer.parseInt(zipCode));
        user.setSkillLevel(skillRating);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    public boolean authenticate(String username, String password) {
        try {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            return passwordEncoder.matches(password, userDetails.getPassword());
        } catch (UsernameNotFoundException e) {
            return false;
        }
    }
}