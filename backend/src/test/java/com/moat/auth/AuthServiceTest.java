package com.moat.auth;

import com.moat.api.model.LoginRequest;
import com.moat.api.model.RegisterRequest;
import com.moat.user.Role;
import com.moat.user.User;
import com.moat.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AuthService authService = new AuthService(userRepository, passwordEncoder);

    @Test
    void register_hashesPasswordAndSetsUserRole() {
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User saved = authService.register(new RegisterRequest().email("alice@example.com").password("password123"));

        assertThat(saved.getEmail()).isEqualTo("alice@example.com");
        assertThat(saved.getRole()).isEqualTo(Role.USER);
        assertThat(saved.getPasswordHash()).isNotEqualTo("password123");
        assertThat(passwordEncoder.matches("password123", saved.getPasswordHash())).isTrue();
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void register_rejectsDuplicateEmail() {
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() ->
                authService.register(new RegisterRequest().email("taken@example.com").password("password123")))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void authenticate_succeedsWithCorrectPassword() {
        User user = new User();
        user.setEmail("bob@example.com");
        user.setRole(Role.USER);
        user.setPasswordHash(passwordEncoder.encode("password123"));
        when(userRepository.findByEmail("bob@example.com")).thenReturn(Optional.of(user));

        User result = authService.authenticate(new LoginRequest().email("bob@example.com").password("password123"));

        assertThat(result.getEmail()).isEqualTo("bob@example.com");
    }

    @Test
    void authenticate_failsWithWrongPassword() {
        User user = new User();
        user.setEmail("bob@example.com");
        user.setRole(Role.USER);
        user.setPasswordHash(passwordEncoder.encode("password123"));
        when(userRepository.findByEmail("bob@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() ->
                authService.authenticate(new LoginRequest().email("bob@example.com").password("wrong-password")))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void authenticate_failsWhenUserNotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                authService.authenticate(new LoginRequest().email("missing@example.com").password("password123")))
                .isInstanceOf(BadCredentialsException.class);
    }
}
