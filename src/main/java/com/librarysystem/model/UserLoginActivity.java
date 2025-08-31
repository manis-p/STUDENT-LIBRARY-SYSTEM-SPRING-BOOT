package com.librarysystem.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class UserLoginActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @com.fasterxml.jackson.annotation.JsonIgnore 
    @JoinColumn(name = "user_id")
    private User user;
    private LocalDateTime loginTime;
    private String ipAddress;
    private String device;
   // private String loginUserNo;
  // private String location;

}
