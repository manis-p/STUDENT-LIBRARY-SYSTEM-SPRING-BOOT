package com.librarysystem.dto;

import java.util.List;

import lombok.Data;

@Data
public class LoginHistoryResponse {
    private long totalLogins;
    private List<LoginUserDto> users;
    
}
