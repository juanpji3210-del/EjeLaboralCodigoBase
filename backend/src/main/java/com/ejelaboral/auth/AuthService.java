package com.ejelaboral.auth;

import com.ejelaboral.auth.dto.AuthResponse;
import com.ejelaboral.auth.dto.LoginRequest;
import com.ejelaboral.auth.dto.RegisterRequest;
import com.ejelaboral.auth.dto.UserResponse;
import com.ejelaboral.common.EmailAlreadyExistsException;
import com.ejelaboral.security.JwtService;
import com.ejelaboral.user.Role;
import com.ejelaboral.user.User;
import com.ejelaboral.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Casos de uso de autenticación (US1, US2, US3).
 * Implementa el contrato specs/001-autenticacion/contracts/auth-api.md.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /** US1: registro con hash BCrypt y unicidad de email (FR-2, FR-3, FR-4). */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        User user = new User();
        user.setEmail(email);
        user.setFullName(request.fullName().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        user.setEnabled(true);

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    /** US2: login validando credenciales con AuthenticationManager (FR-5). */
    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password()));

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));
        return toResponse(user);
    }

    /** US3: perfil del usuario autenticado (FR-8). */
    @Transactional(readOnly = true)
    public UserResponse me(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .map(UserResponse::from)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    }

    private AuthResponse toResponse(User user) {
        return new AuthResponse(
                jwtService.generateToken(user.getEmail()),
                "Bearer",
                UserResponse.from(user));
    }
}
