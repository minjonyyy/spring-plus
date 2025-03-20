package org.example.expert.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @Secured(UserRole.Authority.USER)
    @PutMapping("/users")
    public void changePassword(@AuthenticationPrincipal AuthUser authUser, @RequestBody UserChangePasswordRequest userChangePasswordRequest) {
        userService.changePassword(authUser.getId(), userChangePasswordRequest);
    }

    @PostMapping("/users/{userId}/profile-image")
    public ResponseEntity<String> uploadImage(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestPart("file") MultipartFile file,
            @PathVariable long userId) {

        try {
            userService.uploadImage(userId, authUser, file);
            return ResponseEntity.ok("파일 업로드 성공!");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }

}
