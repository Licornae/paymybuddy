package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<AppUser,Integer> {

    AppUser findByEmail(String email);

    AppUser findByUsername(String username);
}
