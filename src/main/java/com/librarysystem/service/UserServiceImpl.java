package com.librarysystem.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.librarysystem.dto.SignupRequestDto;
import com.librarysystem.dto.UpdateProfileRequestDto;
import com.librarysystem.dto.UserDto;
import com.librarysystem.exception.EmailAlreadyExistsException;
import com.librarysystem.exception.ResourceNotFoundException;
import com.librarysystem.exception.UserNotFoundException;
import com.librarysystem.model.Role;
import com.librarysystem.model.User;
import com.librarysystem.repository.UserLoginActivityRepository;
import com.librarysystem.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements userInterface {

	@Autowired
	UserRepository userRepository;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	private UserLoginActivityRepository userLoginActivityRepository;

	// first method
	public boolean checkIfEmailExists(String email) {
		return userRepository.existsByEmail(email);

	}

	// i have write the test for these method in UserServiceImplTest.java
	// second method
	@Transactional
	@Override
	public User registerUser(SignupRequestDto signupRequestDto) {//need to chnage this method for delete the user
		if (checkIfEmailExists(signupRequestDto.getEmail())) {
			throw new EmailAlreadyExistsException("Email is already taken!");
		}
		// Proceed with saving the user
		User user = new User();
		user.setName(signupRequestDto.getName());
		user.setEmail(signupRequestDto.getEmail());
		user.setPhone(signupRequestDto.getPhone());
		String rawPassword = signupRequestDto.getPassword();
		String encodedPassword = passwordEncoder.encode(rawPassword);
		user.setPassword(encodedPassword);
		user.setRole(Role.ROLE_USER);
		return userRepository.save(user);
	}

	// This method is used for the admin register
	@Transactional
	public User registerAdmin(SignupRequestDto signupRequestDto) {
		if (checkIfEmailExists(signupRequestDto.getEmail())) {
			throw new EmailAlreadyExistsException("Email is already taken!");
		}
		// Proceed with saving the user
		User user = new User();
		user.setName(signupRequestDto.getName());
		user.setEmail(signupRequestDto.getEmail());
		user.setPhone(signupRequestDto.getPhone());
		String rawPassword = signupRequestDto.getPassword();
		String encodedPassword = passwordEncoder.encode(rawPassword);
		user.setPassword(encodedPassword);
		user.setRole(Role.ROLE_ADMIN);
		return userRepository.save(user);
	}

	// THIS IS USED FOR GET THE PROFILE OF USER
	// third method
	@Override
	public User getUserProfile(String email) {
		return userRepository.findByEmail(email)//need to chnage this method for delete the user
				.orElseThrow(() -> new UserNotFoundException("User not found or deleted"));
	}

	// this is for adamin use only
	public User getUserById(Long userId) {//need to chnage this method for delete the user
		return userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found or deleted"));
	}

	// THIS IS USED FOT UPDATE THE USER PROFILE
	// fourth method
	@Override
	public User updateUserProfile(String email, UpdateProfileRequestDto dto) {
		User user = userRepository.findByEmail(email)//need to chnage this method for delete the user
				.orElseThrow(() -> new UserNotFoundException("User not found."));
		user.setName(dto.getName());
		//user.setPassword(dto.getPassword());
		user.setPhone(dto.getPhone());

		return userRepository.save(user);
	}

	// This method is uded for the update the user profile by admain.
	public User updateUser(Long userId, UpdateProfileRequestDto dto) {
		User user = userRepository.findById(userId)//need to chnage this method for delete the user
				.orElseThrow(() -> new UserNotFoundException("User not found"));
		user.setName(dto.getName());
		user.setPhone(dto.getPhone());
		//user.setPassword(dto.getPassword());
		return userRepository.save(user);
	}

	// user delete by ID
	// fifth method
	public void deleteUser(Long userId) {//need to chnage this method for delete the user
		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
		user.setDeleted(true); // soft delete kar rha hu data hide karna na ki delete karna.
		userRepository.save(user);
	}

	// this methos is for Admin
	public void deleteMultipleUsers(List<Long> userIds) {
		List<User> users = userRepository.findAllById(userIds);

		if (users.isEmpty()) {
			throw new UserNotFoundException("No users found with given IDs");
		}

		for (User user : users) {
			user.setDeleted(true); // soft delete
		}

		userRepository.saveAll(users);
	}

	// This method is used to change the user role by admin
	public void changeUserRole(Long id, String role) {//need to chnage this method for delete the user
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

		try {
			Role newRole = Role.valueOf(role); // Convert string to enum
			user.setRole(newRole);
			userRepository.save(user);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid role: " + role);
		}
	}

	public void changeMultipleUserRoles(Map<Long, String> userRoles) {
		List<User> users = userRepository.findAllById(userRoles.keySet());//need to chnage this method for delete the user

		if (users.isEmpty()) {
			throw new ResourceNotFoundException("No users found with given IDs");
		}

		for (User user : users) {
			String roleStr = userRoles.get(user.getId());
			try {
				Role newRole = Role.valueOf(roleStr); // String to Enum
				user.setRole(newRole);
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid role: " + roleStr + " for user ID " + user.getId());
			}
		}

		userRepository.saveAll(users);
	}

	// This method is used to search users by name or email
	public List<UserDto> searchUsers(String keyword) {//need to chnage this method for delete the user
		List<User> users = userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
		return users.stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}

	private UserDto convertToDto(User user) {
		UserDto dto = new UserDto();
		dto.setId(user.getId());
		dto.setName(user.getName());
		dto.setEmail(user.getEmail());
		dto.setRole(user.getRole().name());
		return dto;
	}

	// This method is used to get users by their role
	public List<User> getUsersByRole(Role role) {
		return userRepository.findByRole(role);
	}

	// This method is used to get users by their active status
	public List<User> getUsersByActiveStatus(boolean deleted) {
		return userRepository.findByIsDeleted(deleted);
	}

	// uerd for no. of unique login user
	public long getUniqueLoginUserCount() {
		return userLoginActivityRepository.countDistinctUsersLoggedIn();

	}

}
