package com.librarysystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.librarysystem.model.Role;
import com.librarysystem.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
	boolean existsByEmail(String email);

	Optional<User> findByEmail(String email);

	Optional<User> findById(Long id);

	List<User>  findAllById(Iterable<Long> ids);

	List<User> findAllByIsDeletedFalse();// user not soft deted

	Optional<User> findByEmailAndIsDeletedFalse(String email);

	Optional<User> findByIdAndIsDeletedFalse(Long id);

	List<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);

	List<User> findAllByOrderByLastLoginDesc();

	List<User> findByRole(Role role);

    List<User> findByIsDeleted(boolean isDeleted);

	List<User> findAll();

	@Query("SELECT COUNT(DISTINCT ula.user.id) FROM UserLoginActivity ula")
	long countDistinctUsersLoggedIn();

}
