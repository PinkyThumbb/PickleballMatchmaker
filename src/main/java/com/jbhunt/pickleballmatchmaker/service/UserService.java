package com.jbhunt.pickleballmatchmaker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final InMemoryUserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(InMemoryUserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }

    public void saveUser(String username, String password) {
        if (userDetailsManager.userExists(username)) {
            throw new IllegalArgumentException("User already exists");
        }
        UserDetails user = User.withUsername(username)
                .password(passwordEncoder.encode(password))
                .roles("USER")
                .build();
        userDetailsManager.createUser(user);
    }

    public boolean authenticate(String username, String password) {
        UserDetails userDetails = userDetailsManager.loadUserByUsername(username);
        return userDetails != null && passwordEncoder.matches(password, userDetails.getPassword());
    }
}