package com.librarysystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.librarysystem.model.UserLoginActivity;

@Repository
public interface UserLoginActivityRepository extends CrudRepository<UserLoginActivity, Long> {
    List<UserLoginActivity> findByUserIdOrderByLoginTimeDesc(Long id);
    @Query("SELECT COUNT(DISTINCT ula.user.id) FROM UserLoginActivity ula")
	long countDistinctUsersLoggedIn();
}
