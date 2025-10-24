package it.auth.travelauth.service;

import org.springframework.stereotype.Service;

import it.auth.travelauth.domain.dto.request.LoginRequestDto;
import it.auth.travelauth.domain.dto.request.UserProfileUpdateRequestDto;
import it.auth.travelauth.domain.dto.request.UserRegistrationRequestDto;
import it.auth.travelauth.domain.dto.response.LoginResponseDto;
import it.auth.travelauth.domain.dto.response.UserRegistrationResponseDto;
import it.auth.travelauth.domain.dto.response.UserResponseDto;
import jakarta.transaction.Transactional;

@Service
@Transactional
public interface UserService {

    public UserRegistrationResponseDto createUser(UserRegistrationRequestDto userRegistrationRequestDto)
            throws Exception;

    public LoginResponseDto login(LoginRequestDto loginRequestDto);

    public UserResponseDto getProfile(String token);

    public UserResponseDto updateProfile(String token, UserProfileUpdateRequestDto updateDto);
}
