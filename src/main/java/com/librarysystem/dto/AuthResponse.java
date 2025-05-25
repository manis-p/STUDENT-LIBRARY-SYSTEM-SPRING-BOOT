package com.librarysystem.dto;

public class AuthResponse {
	 private String token;
        // this is only for return the jwt token 
	    public AuthResponse(String token) {
	        this.token = token;
	    }

	    public String getToken() {
	        return token;
	    }
}
