package com.testtask.project.controller;

import com.testtask.project.entity.User;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Value("${user.minAge}")
    private int minAge = 18;
    private long userIdCounter = 1;
    private List<User> userList = new ArrayList<>();

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userList);
    }

    @PostMapping
    public ResponseEntity<String> createUser(@Valid @RequestBody User user) {
        LocalDate eighteenYearsAgo = LocalDate.now().minusYears(minAge);
        if (user.getBirthDate().isAfter(eighteenYearsAgo)) {
            return ResponseEntity.badRequest().body("User must be at least 18 years old.");
        }
        user.setId(userIdCounter++);
        userList.add(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully.");
    }

    @PutMapping("/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable("userId") Long userId, @RequestBody User user) {
        User existingUser = findUserById(userId);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }
        existingUser.setEmail(user.getEmail());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setBirthDate(user.getBirthDate());
        existingUser.setAddress(user.getAddress());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        return ResponseEntity.ok("User updated successfully.");
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") Long userId) {
        User existingUser = findUserById(userId);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }
        userList.remove(existingUser);
        return ResponseEntity.ok("User deleted successfully.");
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsersByBirthDateRange(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        if (fromDate.isAfter(toDate)) {
            return ResponseEntity.badRequest().body(new ArrayList<>());
        }

        List<User> usersInRange = new ArrayList<>();
        for (User user : userList) {
            LocalDate userBirthDate = user.getBirthDate();
            if (userBirthDate.isAfter(fromDate) && userBirthDate.isBefore(toDate)) {
                usersInRange.add(user);
            }
        }
        return ResponseEntity.ok(usersInRange);
    }

    private User findUserById(Long userId) {
        for (User user : userList) {
            if (user.getId().equals(userId)) {
                return user;
            }
        }
        return null;
    }
}

