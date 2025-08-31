package com.librarysystem.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.librarysystem.dto.UpdateProfileRequestDto;
import com.librarysystem.dto.UserDto;
import com.librarysystem.email.UserService;
import com.librarysystem.model.Role;
import com.librarysystem.model.User;
import com.librarysystem.model.UserLoginActivity;
import com.librarysystem.repository.UserLoginActivityRepository;
import com.librarysystem.repository.UserRepository;
import com.librarysystem.service.UserServiceImpl;

@RestController
@RequestMapping("/api/admin") // base url for admin operations
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserService userServiceemail;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserLoginActivityRepository userLoginActivityRepository;

    // Get single user by ID
    // in the admin panel there is 17 end point
    @GetMapping("/single/users/{id}") // donbe
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        System.out.println("Fetching user with ID: " + id);
        return ResponseEntity.ok(userService.getUserById(id));

    }

    // Delete user
    @DeleteMapping("/delete/users/{id}") // done
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully!");
    }

    // Delete multiple users
    @DeleteMapping("/delete/multiple/users") // done
    public ResponseEntity<String> deleteMultipleUsers(@RequestBody List<Long> ids) {
        userService.deleteMultipleUsers(ids);
        return ResponseEntity.ok("Users deleted successfully!");
    }

    // Update user info
    @PutMapping("/update/users/{id}") // done
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UpdateProfileRequestDto dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    // this is used to change the role of the user
    @PutMapping("/admin/users/{id}/role/change") // done
    public ResponseEntity<String> changeRole(@PathVariable Long id, @RequestParam String role) {

        userService.changeUserRole(id, role);
        return ResponseEntity.ok("User role updated");
    }

    // this is used to change the role of multiple user at once
    @PutMapping("/admin/users/roles/chnage/mutiple") // done
    public ResponseEntity<String> changeMultipleUserRoles(@RequestBody Map<Long, String> userRoles) {
        userService.changeMultipleUserRoles(userRoles);
        return ResponseEntity.ok("User roles updated successfully!");
    }

    // search user by name or email
    @GetMapping("/admin/users/search") // done
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String keyword) {
        return ResponseEntity.ok(userService.searchUsers(keyword));
    }

    // filter by role and the status like role like admin or user
    @GetMapping("filterby/role/{role}") // done
    public List<User> getUsersByRole(@PathVariable Role role) {
        return userService.getUsersByRole(role);
    }

    // user Activity tracking
    // user last login
    // login device
    // login location
    // how many people login
    // this is used to get all the users
    @GetMapping("/all/users") // done
    public List<User> getAllUsers() { // done
        return userRepository.findAll();
    }

    // this is used to get the login history of the user
    @GetMapping("loginhistory/{userId}") // done
    public List<UserLoginActivity> getUserLoginHistory(@PathVariable Long userId) {
        return userLoginActivityRepository.findByUserIdOrderByLoginTimeDesc(userId);
    }

    // this is used to get the unique login user count
    @GetMapping("/unique-login-users-count") // done
    public long getUniqueLoginUsersCount() {
        return userService.getUniqueLoginUserCount();
    }

    // Security Control krna hai .in this method we will send reset link to the user
    @PostMapping("/force-send-reset-link/{userId}") // done
    public ResponseEntity<String> sendResetLink(@PathVariable Long userId) {
        userServiceemail.forceSendResetLink(userId);
        return ResponseEntity.ok("Reset link sent successfully!");
    }

}
//  need to chanage in the update bcz there i has impleted the used can updated their password that is not good.