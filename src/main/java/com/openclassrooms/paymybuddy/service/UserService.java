package com.openclassrooms.paymybuddy.service;

import org.springframework.stereotype.Service;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.openclassrooms.paymybuddy.model.AppUser;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public AppUser saveUser(AppUser user) {
        if (userRepository.findByEmail(user.getEmail()) != null){
            throw new IllegalArgumentException("Email already exists");
        }

        if (userRepository.findByUsername(user.getUsername()) != null){
            throw new IllegalArgumentException("Username already exists");
        }
        return userRepository.save(user);
    }

}
