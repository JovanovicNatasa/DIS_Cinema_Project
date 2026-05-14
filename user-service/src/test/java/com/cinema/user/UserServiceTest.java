package com.cinema.user;

import com.cinema.user.dto.RegisterRequest;
import com.cinema.user.dto.AuthResponse;
import com.cinema.user.model.Role;
import com.cinema.user.model.User;
import com.cinema.user.repository.RoleRepository;
import com.cinema.user.repository.UserRepository;
import com.cinema.user.security.JwtService;
import com.cinema.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    private Role defaultRole;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        defaultRole = new Role();
        defaultRole.setId(1L);
        defaultRole.setRoleName("USER");

        registerRequest = new RegisterRequest();
        registerRequest.setFirstName("John");
        registerRequest.setEmail("john@example.com");
        registerRequest.setPassword("password123");
    }

    @Test
    void register_ShouldReturnAuthResponse_WhenValidRequest() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByRoleName("USER")).thenReturn(Optional.of(defaultRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
        when(jwtService.generateToken(anyString(), anyString())).thenReturn("mockToken");

        AuthResponse response = userService.register(registerRequest);

        assertNotNull(response);
        assertEquals("john@example.com", response.getEmail());
        assertEquals("USER", response.getRoleName());
        assertNotNull(response.getToken());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.register(registerRequest));

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        User user = new User();
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");
        user.setRole(defaultRole);

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        var userDetails = userService.loadUserByUsername("john@example.com");

        assertNotNull(userDetails);
        assertEquals("john@example.com", userDetails.getUsername());
    }
}