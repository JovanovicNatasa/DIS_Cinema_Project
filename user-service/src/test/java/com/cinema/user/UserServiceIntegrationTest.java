package com.cinema.user;

import com.cinema.user.dto.AuthResponse;
import com.cinema.user.dto.LoginRequest;
import com.cinema.user.dto.RegisterRequest;
import com.cinema.user.model.Role;
import com.cinema.user.repository.RoleRepository;
import com.cinema.user.repository.UserRepository;
import com.cinema.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Testcontainers
public class UserServiceIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("userdb_test")
            .withUsername("postgres")
            .withPassword("postgres");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        // Disable Eureka and Config Server for integration tests — we only test the DB layer
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.cloud.config.enabled", () -> "false");
        registry.add("spring.cloud.discovery.enabled", () -> "false");
    }

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role userRole = new Role();
        userRole.setRoleName("USER");
        roleRepository.save(userRole);
    }

    @Test
    void register_ShouldPersistUserInRealDatabase() {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("Ana");
        request.setEmail("ana@cinema.com");
        request.setPassword("Password123!");

        AuthResponse response = userService.register(request);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("ana@cinema.com", response.getEmail());
        assertEquals("USER", response.getRoleName());
        assertTrue(userRepository.existsByEmail("ana@cinema.com"));
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExistsInDatabase() {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("Ana");
        request.setEmail("ana@cinema.com");
        request.setPassword("Password123!");
        userService.register(request);

        RegisterRequest duplicateRequest = new RegisterRequest();
        duplicateRequest.setFirstName("Drugi");
        duplicateRequest.setEmail("ana@cinema.com");
        duplicateRequest.setPassword("Password123!");

        assertThrows(RuntimeException.class, () -> userService.register(duplicateRequest));
        assertEquals(1, userRepository.count());
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreCorrect() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Ana");
        registerRequest.setEmail("ana@cinema.com");
        registerRequest.setPassword("Password123!");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("ana@cinema.com");
        loginRequest.setPassword("Password123!");

        AuthResponse response = userService.login(loginRequest);

        assertNotNull(response.getToken());
        assertEquals("ana@cinema.com", response.getEmail());
    }

    @Test
    void login_ShouldThrowException_WhenPasswordIsWrong() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Ana");
        registerRequest.setEmail("ana@cinema.com");
        registerRequest.setPassword("Password123!");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("ana@cinema.com");
        loginRequest.setPassword("WrongPassword123!");

        assertThrows(BadCredentialsException.class, () -> userService.login(loginRequest));
    }
}