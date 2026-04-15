package io.eikon.authservice.service;

import io.eikon.authservice.dto.UserDTO;
import io.eikon.authservice.dto.UserUpdateDTO;
import io.eikon.authservice.entity.User;
import io.eikon.authservice.exception.ResourceNotFoundException;
import io.eikon.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });

        log.info("Fetched user profile: {}", user.getEmail());
        return mapToUserDTO(user);
    }

    @Transactional
    public UserDTO updateProfile(UUID id, UserUpdateDTO request) {
        // Verify user exists
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Update failed: User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });

        // Update non-sensitive fields
        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            user.setLastName(request.getLastName());
        }

        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        // Only allow password update if provided
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            log.info("Password updated for user: {}", user.getEmail());
        }

        // Save updated user
        User updatedUser = userRepository.save(user);
        log.info("User profile updated: {}", updatedUser.getEmail());

        return mapToUserDTO(updatedUser);
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
