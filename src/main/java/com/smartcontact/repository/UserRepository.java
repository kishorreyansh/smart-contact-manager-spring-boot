package com.smartcontact.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartcontact.entities.User;

public interface UserRepository extends JpaRepository<User, Integer>{

}
