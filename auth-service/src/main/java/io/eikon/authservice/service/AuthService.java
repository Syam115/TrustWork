package io.eikon.authservice.service;

import io.eikon.authservice.config.JwtUtil;
import io.eikon.authservice.dto.AuthResponse;
import io.eikon.authservice.dto.LoginRequest;
import io.eikon.authservice.dto.RegisterRequest;
import io.eikon.authservice.dto.UserDTO;
import io.eikon.authservice.entity.Role;
import io.eikon.authservice.entity.User;
import io.eikon.authservice.exception.ResourceNotFoundException;
import io.eikon.authservice.exception.UserAlreadyExistsException;
import io.eikon.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public UserDTO register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration attempt for existing email: {}", request.getEmail());
            throw new UserAlreadyExistsException("A user with this email already exists");
        }

        // Map DTO to Entity
        User user = User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword())) // Secure: Hash the password
                .role(Role.valueOf(request.getRole().toUpperCase()))
                .isActive(true)
                .build();

        // Save to database
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with email: {}", savedUser.getEmail());

        // Return mapped UserDTO
        return mapToUserDTO(savedUser);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Authenticate: Use AuthenticationManager to verify credentials
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // If authentication fails, throw exception
        if (!authentication.isAuthenticated()) {
            log.warn("Authentication failed for email: {}", request.getEmail());
            throw new ResourceNotFoundException("Invalid credentials");
        }

        // Identify: Fetch user from database
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found after authentication"));

        // Generate JWT token with user details
        String token = jwtUtil.generateToken(user.getId().toString(), request.getEmail(), user.getRole().name());
        log.info("Token generated successfully for user: {}", user.getEmail());

        // Return AuthResponse with token and user metadata
        return AuthResponse.builder()
                .userId(user.getId().toString())
                .email(user.getEmail())
                .role(user.getRole().name())
                .accessToken(token)
                .tokenType("Bearer")
                .build();
    }

    private UserDTO mapToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .bio(user.getBio())
                .build();
    }
}
