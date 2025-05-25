package com.librarysystem.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.librarysystem.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
	 boolean existsByEmail(String email);
	 Optional<User> findByEmail(String email); 
}
