package com.librarysystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.librarysystem.dto.SignupRequestDto;
import com.librarysystem.dto.UpdateProfileRequestDto;
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
	
	  public boolean checkIfEmailExists(String email) {
	        return userRepository.existsByEmail(email); // Check if email exists
	    }
	  @Transactional
	  public User registerUser(SignupRequestDto signupRequestDto) {
	        if (checkIfEmailExists(signupRequestDto.getEmail())) {
	            throw new RuntimeException("Email is already taken!");
	        }
	        // Proceed with saving the user
	        User user = new User();
	       user.setName(signupRequestDto.getName());
	       user.setEmail(signupRequestDto.getEmail());
	       user.setPhone(signupRequestDto.getPhone());
	       String rawPassword = signupRequestDto.getPassword();
	       String encodedPassword = passwordEncoder.encode(rawPassword);
	       user.setPassword(encodedPassword);
	       user.setRole(signupRequestDto.getRole());   
	       //System.out.println(rawPassword);
	      return  userRepository.save(user);
	    }
	/*   no used 
	@Override
	public User loginUser(LoginRequestDto dto) {
		User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(dto.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user;
		
	}
	*/
	//THIS IS USED FOR GET   THE PROFILE OF USER 
	@Override
	public User getUserProfile(String email) {
	return	userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
	}
	
	// THIS IS USED FOT UPDATE THE USER PROFILE
	@Override
	public User updateUserProfile(String email, UpdateProfileRequestDto dto) {
		         User user =  userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("user not found"));
		         user.setName(dto.getName());
		         user.setPassword(dto.getPassword());
		         user.setPhone(dto.getPhone());
		         
		return userRepository.save(user)  ;
	}
	
	
	

}
