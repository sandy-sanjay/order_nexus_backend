package com.ordernexus.order_nexus_backend.auth;

import com.ordernexus.order_nexus_backend.security.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @Value("${google.client.id}")
    private String googleClientId;

    public AuthController(JwtUtil jwtUtil, AuthService authService, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    // ✅ GOOGLE LOGIN
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest request) {
        try {
            NetHttpTransport transport = new NetHttpTransport();
            GsonFactory jsonFactory = GsonFactory.getDefaultInstance();

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.getIdToken());
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();

                // Check if user exists, if not create
                Optional<AuthUser> userOpt = authService.findByUsername(email);
                AuthUser user;
                if (userOpt.isEmpty()) {
                    user = new AuthUser();
                    user.setUsername(email);

                    // ✅ ROLE LOGIC: Only this email becomes ADMIN
                    if (email.equalsIgnoreCase("sandysanjay589@gmail.com")) {
                        user.setRole("ADMIN");
                    } else {
                        user.setRole("USER");
                    }

                    authService.save(user);
                } else {
                    user = userOpt.get();
                }

                String token = jwtUtil.generateToken(user.getUsername());
                return ResponseEntity.ok(new LoginResponse(token, user.getUsername(), user.getRole()));

            } else {
                return ResponseEntity.status(401).body("Invalid Google ID Token");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error verifying Google token: " + e.getMessage());
        }
    }

    // ✅ REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthUser user, BindingResult result) {

        // Handle validation errors
        if (result.hasErrors()) {
            String errorMessage = result.getFieldErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(errorMessage);
        }

        if (authService.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("User already exists");
        }

        // ✅ ROLE LOGIC: Only this email becomes ADMIN
        if (user.getUsername().equalsIgnoreCase("sandysanjay589@gmail.com")) {
            user.setRole("ADMIN");
        } else {
            user.setRole("USER");
        }

        authService.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    // ✅ LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        System.out.println("Login request received for: " + request.getUsername());

        var userOpt = authService.findByUsername(request.getUsername());

        if (userOpt.isEmpty()) {
            System.out.println("User not found: " + request.getUsername());
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        AuthUser user = userOpt.get();

        if (user.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            System.out.println("Password mismatch for: " + request.getUsername());
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        System.out.println("Login successful for: " + request.getUsername());
        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(new LoginResponse(token, user.getUsername(), user.getRole()));
    }
}
