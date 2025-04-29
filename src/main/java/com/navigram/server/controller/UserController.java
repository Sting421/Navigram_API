package com.navigram.server.controller;

import com.navigram.server.dto.UserDto;
import com.navigram.server.model.Role;
import com.navigram.server.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

@GetMapping("/all")
public ResponseEntity<List<UserDto>> getAllUsers() {
    List<UserDto> users = userService.getAllUsers();
    List<UserDto> userRoleOnly = users.stream()
            .filter(user -> user.getRole() == Role.USER)
            .toList();
    return ResponseEntity.ok(userRoleOnly);
}

@GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<String> getUserProfile(@PathVariable String id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user.getProfilePicture());
    }

    @GetMapping("/{id}/ban-status")
    public ResponseEntity<Map<String, Object>> getUserBanStatus(@PathVariable String id) {
        UserDto user = userService.getUserById(id);
        Map<String, Object> response = new HashMap<>();
        
        if (!user.isEnabled()) {
            response.put("banned", true);
            response.put("banEndDate", user.getBanEndDate());
            response.put("banReason", "Account has been suspended by moderator or administrator");
        } else {
            response.put("banned", false);
        }
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable String id,
            @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<Map<String, Object>> followUser(
            @PathVariable String id,
            Authentication authentication) {
        String currentUserId = userService.getUserByUsername(authentication.getName()).getId();
        userService.followUser(currentUserId, id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Successfully followed user");
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/unfollow")
    public ResponseEntity<Map<String, Object>> unfollowUser(
            @PathVariable String id,
            Authentication authentication) {
        String currentUserId = userService.getUserByUsername(authentication.getName()).getId();
        userService.unfollowUser(currentUserId, id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Successfully unfollowed user");
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/following")
    public ResponseEntity<Map<String, Object>> getFollowedUsers(Authentication authentication) {
        String currentUserId = userService.getUserByUsername(authentication.getName()).getId();
        List<UserDto> followedUsers = userService.getFollowedUsers(currentUserId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Successfully retrieved followed users");
        response.put("data", followedUsers);
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/follow-counts")
    public ResponseEntity<Map<String, Object>> getFollowCounts(@PathVariable String id) {
        Map<String, Integer> counts = userService.getFollowCounts(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Successfully retrieved follow counts");
        response.put("data", counts);
        response.put("success", true);
        return ResponseEntity.ok(response);
    }
}
