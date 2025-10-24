package it.auth.travelauth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.auth.travelauth.domain.dto.request.LoginRequestDto;
import it.auth.travelauth.domain.dto.request.UserProfileUpdateRequestDto;
import it.auth.travelauth.domain.dto.request.UserRegistrationRequestDto;
import it.auth.travelauth.domain.dto.response.LoginResponseDto;
import it.auth.travelauth.domain.dto.response.UserRegistrationResponseDto;
import it.auth.travelauth.domain.dto.response.UserResponseDto;
import it.auth.travelauth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequestDto) {
        return userService.login(loginRequestDto);
    }

    @PostMapping("/register")
    public UserRegistrationResponseDto register(
            @Valid @RequestBody UserRegistrationRequestDto userRegistrationRequestDto)
            throws Exception {
        return userService.createUser(userRegistrationRequestDto);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDto> getUserProfile(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        UserResponseDto userProfile = userService.getProfile(token);
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponseDto> updateUserProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserProfileUpdateRequestDto updateDto) {

        String token = authHeader.replace("Bearer ", "");
        UserResponseDto updatedProfile = userService.updateProfile(token, updateDto);
        return ResponseEntity.ok(updatedProfile);
    }
}
