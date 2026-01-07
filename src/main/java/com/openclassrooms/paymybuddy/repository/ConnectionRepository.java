package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.Connection;
import com.openclassrooms.paymybuddy.model.ConnectionId;
import org.springframework.data.repository.CrudRepository;

public interface ConnectionRepository extends CrudRepository<Connection, ConnectionId> {
}
