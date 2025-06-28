package com.librarysystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.librarysystem.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
	boolean existsByEmail(String email);

	Optional<User> findByEmail(String email);

	Optional<User> findById(Long userId);

	List<User> findAllByIsDeletedFalse();//user not  soft deted

	Optional<User> findByEmailAndIsDeletedFalse(String email);

	Optional<User> findByIdAndIsDeletedFalse(Long id);


}
