package com.librarysystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.librarysystem.model.RefreshToken;
import com.librarysystem.model.User;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long>{
	
	
    Optional<RefreshToken> findByToken(String token);//   find the token from DB

    List<RefreshToken> findAllByUser(User user);  // check how many devices are login 

    int deleteByUser(User user);   // for multiple token 

    int deleteByToken(String token);// for single token 
    
	 boolean existsByToken(String email);



}
