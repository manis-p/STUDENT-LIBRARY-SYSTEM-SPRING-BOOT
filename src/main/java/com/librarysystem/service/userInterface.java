package com.librarysystem.service;

import com.librarysystem.dto.SignupRequestDto;
import com.librarysystem.dto.UpdateProfileRequestDto;
import com.librarysystem.model.User;
// this the interface is used for user related operations.

public interface userInterface {

    User registerUser(SignupRequestDto signupRequestDto);

    //User loginUser(LoginRequestDto dto);
    User getUserProfile(String email);

    User updateUserProfile(String email, UpdateProfileRequestDto dto);
}
