package io.eikon.authservice.controller;

import io.eikon.authservice.config.JwtUtil;
import io.eikon.authservice.config.UserPrincipal;
import io.eikon.authservice.dto.AuthResponse;
import io.eikon.authservice.dto.LoginRequest;
import io.eikon.authservice.dto.RegisterRequest;
import io.eikon.authservice.dto.UserDTO;
import io.eikon.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(request));
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validate(@RequestParam("token") String token, @AuthenticationPrincipal UserPrincipal principal) {
        boolean isValid = jwtUtil.validateToken(token, principal);

        if (isValid) {
            String username = jwtUtil.extractUsername(token);
            return ResponseEntity.status(HttpStatus.OK).body(username);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
