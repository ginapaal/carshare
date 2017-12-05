package com.codecool.carshare.repository;

import com.codecool.carshare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer>{
    User getUserByName(@Param("name") String name);
}
