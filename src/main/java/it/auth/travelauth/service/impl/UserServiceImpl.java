package it.auth.travelauth.service.impl;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import it.auth.travelauth.domain.dto.request.LoginRequestDto;
import it.auth.travelauth.domain.dto.request.UserRegistrationRequestDto;
import it.auth.travelauth.domain.dto.response.LoginResponseDto;
import it.auth.travelauth.domain.dto.response.UserRegistrationResponseDto;
import it.auth.travelauth.domain.entity.User;
import it.auth.travelauth.domain.entity.UserProfile;
import it.auth.travelauth.domain.util.JwtUtil;
import it.auth.travelauth.repository.UserProfileRepository;
import it.auth.travelauth.repository.UserRepository;
import it.auth.travelauth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    private final ModelMapper modelMapper;

    public UserRegistrationResponseDto createUser(UserRegistrationRequestDto userRegistrationRequestDto)
            throws Exception {

        // Verifico se esiste un utente con quelle credenziali
        User userFound = findUserByUsernameAndEmail(userRegistrationRequestDto.getUsername(),
                userRegistrationRequestDto.getEmail());
        if (userFound != null) {
            throw new Exception("Utente già esistente");
        }

        UserProfile userProfile = UserProfile
                .builder()
                .email(userRegistrationRequestDto.getEmail())
                .firstName(userRegistrationRequestDto.getFirstName())
                .lastName(userRegistrationRequestDto.getLastName())
                .build();

        userProfile = userProfileRepository.save(userProfile);

        System.out.println(userRegistrationRequestDto);

        User user = User
                .builder()
                .userProfile(userProfile)
                .username(userRegistrationRequestDto.getUsername())
                .password(new BCryptPasswordEncoder().encode(userRegistrationRequestDto.getPassword()))
                .build();

        User savedUser = insert(user);
        System.out.println(savedUser); // stampa per verificare che i dati ci siano
        return modelMapper.map(savedUser, UserRegistrationResponseDto.class);

    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(),
                        loginRequestDto.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .username(loginRequestDto.getUsername())
                .token(jwtUtil.generateToken(userDetails.getUsername()))
                .build();
        return loginResponseDto;

    }

    private User findUserByUsernameAndEmail(String username, String email) {
        Optional<User> optionalUser = userRepository.findByUsernameAndDeleteDateIsNull(username);

        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }

        return null;
    }

    private User insert(User user) {
        user = userRepository.save(user);
        log.info("User creato {}", user);
        return user;
    }

}
