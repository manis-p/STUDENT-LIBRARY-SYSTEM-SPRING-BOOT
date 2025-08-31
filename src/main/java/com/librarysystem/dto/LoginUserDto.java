package com.librarysystem.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LoginUserDto {

    private Long id;
    private String name;
    private String email;
    private LocalDateTime loginTime;
}
