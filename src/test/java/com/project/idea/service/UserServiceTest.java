package com.project.idea.service;

import com.project.idea.model.User1;
import com.project.idea.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User1 user;

    @BeforeEach
    void setUp() {
        user = new User1();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("rawPassword");
        user.setRole("ROLE_STUDENT");
    }

    @Test
    void testSaveUser() {
        // Mock password encoding
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");

        // Call the method
        userService.save(user);

        // Verify password was encoded and user was saved
        verify(passwordEncoder).encode("rawPassword");
        verify(userRepo).save(user);

        assertEquals("encodedPassword", user.getPassword());
    }

    @Test
    void testLoadUserByUsername_Success() {
        // Mock repository call
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Call the method
        UserDetails userDetails = userService.loadUserByUsername("testuser");

        // Verify results
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertEquals("ROLE_STUDENT", userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Mock repository call to return empty
        when(userRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        // Verify exception is thrown
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("unknown");
        });
    }

    @Test
    void testSaveUserWithNullPassword() {
        user.setPassword(null);

        when(passwordEncoder.encode(null)).thenThrow(new IllegalArgumentException("Password cannot be null"));

        assertThrows(IllegalArgumentException.class, () -> {
            userService.save(user);
        });
    }
}