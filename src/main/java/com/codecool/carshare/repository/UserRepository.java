package com.codecool.carshare.repository;

import com.codecool.carshare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer>{

    User findUserById(Integer id);
}
