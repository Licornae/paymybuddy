package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.AppUser;
import com.openclassrooms.paymybuddy.model.Connection;
import com.openclassrooms.paymybuddy.repository.ConnectionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConnectionService {

    @Autowired
    ConnectionRepository connectionRepository;

    public void addConnection(AppUser user, AppUser friend) {

        if (user.equals(friend)) {
            throw new IllegalArgumentException("Cannot add yourself as a friend");
        }

        if (connectionRepository.existsByUserAndFriend(user, friend)) {
            throw new IllegalArgumentException("Connection already exists");
        }

        Connection connection = new Connection();
        connection.setUser(user);
        connection.setFriend(friend);

        connectionRepository.save(connection);
    }
}
