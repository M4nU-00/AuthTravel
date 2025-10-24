package it.auth.travelauth.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import it.auth.travelauth.domain.dto.request.LoginRequestDto;
import it.auth.travelauth.domain.dto.request.UserProfileUpdateRequestDto;
import it.auth.travelauth.domain.dto.request.UserRegistrationRequestDto;
import it.auth.travelauth.domain.dto.response.LoginResponseDto;
import it.auth.travelauth.domain.dto.response.UserRegistrationResponseDto;
import it.auth.travelauth.domain.dto.response.UserResponseDto;
import it.auth.travelauth.domain.entity.User;
import it.auth.travelauth.domain.entity.UserProfile;
import it.auth.travelauth.domain.util.JwtUtil;
import it.auth.travelauth.repository.UserProfileRepository;
import it.auth.travelauth.repository.UserRepository;
import it.auth.travelauth.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    private final ModelMapper modelMapper;

    @Override
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
        return modelMapper.map(savedUser, UserRegistrationResponseDto.class);

    }

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(),
                        loginRequestDto.getPassword()));

        // Recupero l'oggetto UserDetails
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Recupero lo user (faccio direttamente la get perchè se va a buon fine il
        // login sicuro l'utente c'è)
        User user = userRepository.findByUsername(userDetails.getUsername()).get();

        // Creo l'oggetto map
        Map<String, Object> claims = new HashMap<>();
        claims.put("uuidUser", user.getUuid().toString());
        claims.put("username", user.getUsername());
        claims.put("email", user.getUserProfile().getEmail());
        claims.put("firstName", user.getUserProfile().getFirstName());
        claims.put("lastName", user.getUserProfile().getLastName());

        // Creo l'oggetto di response e genero il token
        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .username(loginRequestDto.getUsername())
                .token(jwtUtil.generateToken(userDetails.getUsername(), claims))
                .build();
        return loginResponseDto;

    }

    @Override
    public UserResponseDto getProfile(String token) {
        // Recupero l'username
        String username = jwtUtil.extractUsername(token);

        // Recupero lo User
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato"));

        UserProfile profile = user.getUserProfile();

        // Mappo l'oggetto userResponseDto
        UserResponseDto userResponseDto = modelMapper.map(profile, UserResponseDto.class);
        userResponseDto.setUuidUser(profile.getUser().getUuid());
        userResponseDto.setUsername(profile.getUser().getUsername());
        return userResponseDto;
    }

    @Override
    public UserResponseDto updateProfile(String token, UserProfileUpdateRequestDto updateDto) {
        String username = jwtUtil.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato"));

        UserProfile profile = user.getUserProfile();

        // Aggiorna i campi
        profile.setFirstName(updateDto.getFirstName());
        profile.setLastName(updateDto.getLastName());
        profile.setEmail(updateDto.getEmail());

        // Salva le modifiche
        userProfileRepository.save(profile);

        // Mappa la risposta
        UserResponseDto responseDto = modelMapper.map(profile, UserResponseDto.class);
        responseDto.setUuidUser(user.getUuid());

        return responseDto;
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
