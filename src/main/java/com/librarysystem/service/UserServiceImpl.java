package com.librarysystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.librarysystem.dto.SignupRequestDto;
import com.librarysystem.dto.UpdateProfileRequestDto;
import com.librarysystem.exception.EmailAlreadyExistsException;
import com.librarysystem.exception.UserNotFoundException;
import com.librarysystem.model.Role;
import com.librarysystem.model.User;
import com.librarysystem.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements userInterface {

	@Autowired
	UserRepository userRepository;
	@Autowired
	PasswordEncoder passwordEncoder;
    //first method
	public boolean checkIfEmailExists(String email) {
		return userRepository.existsByEmail(email); // Check if email exists
	}
	// i have write the test  for these method in UserServiceImplTest.java
   //second method
	@Transactional
	@Override
	public User registerUser(SignupRequestDto signupRequestDto) {
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

	// THIS IS USED FOR GET THE PROFILE OF USER
	//third method
	@Override
	public User getUserProfile(String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
	}

	// THIS IS USED FOT UPDATE THE USER PROFILE
	// fourth method
	@Override
	public User updateUserProfile(String email, UpdateProfileRequestDto dto) {
		User user = userRepository.findByEmailAndIsDeletedFalse(email)
				.orElseThrow(() -> new UserNotFoundException("User not found."));
		user.setName(dto.getName());
		user.setPassword(dto.getPassword());
		user.setPhone(dto.getPhone());

		return userRepository.save(user);
	}

	// user delete by ID
       // fifth method
	public void deleteUser(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
		user.setDeleted(true); // soft delete kar rha hu data hide karna na ki delete karna.
		userRepository.save(user);
	}

}
