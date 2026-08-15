package com.blooddonation.service;

import com.blooddonation.dto.AuthResponseDTO;
import com.blooddonation.dto.LoginRequestDTO;
import com.blooddonation.dto.RegisterRequestDTO;
import com.blooddonation.dto.UserResponseDTO;
import com.blooddonation.exception.ResourceNotFoundException;
import com.blooddonation.exception.ValidationException;
import com.blooddonation.model.User;
import com.blooddonation.model.UserRole;
import com.blooddonation.repository.UserRepository;
import com.blooddonation.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AuthResponseDTO register(RegisterRequestDTO registerDTO) {
        if (registerDTO == null) {
            throw new ValidationException("Registration data cannot be null");
        }

        String email = registerDTO.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new ValidationException("Email is already registered: " + email);
        }

        UserRole role = registerDTO.getRole() != null ? registerDTO.getRole() : UserRole.DONOR;

        User user = new User(
                registerDTO.getName().trim(),
                email,
                passwordEncoder.encode(registerDTO.getPassword()),
                registerDTO.getPhone() != null ? registerDTO.getPhone().trim() : null,
                role
        );

        User savedUser = userRepository.save(user);

        String token = jwtTokenProvider.generateToken(savedUser.getEmail(), savedUser.getRole());

        return new AuthResponseDTO(
                token,
                jwtTokenProvider.getExpirationTime(),
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    public AuthResponseDTO login(LoginRequestDTO loginDTO) {
        if (loginDTO == null) {
            throw new ValidationException("Login credentials cannot be null");
        }

        String email = loginDTO.getEmail().trim().toLowerCase();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, loginDTO.getPassword())
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        String token = jwtTokenProvider.generateToken(authentication);

        return new AuthResponseDTO(
                token,
                jwtTokenProvider.getExpirationTime(),
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return UserResponseDTO.fromEntity(user);
    }
}
