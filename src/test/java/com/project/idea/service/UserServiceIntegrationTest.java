package com.project.idea.service;

import static org.junit.jupiter.api.Assertions.*;

import com.project.idea.model.User1;
import com.project.idea.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class UserServiceIntegrationTest {

  @Autowired private UserService userService;

  @Autowired private UserRepository userRepo;

  @Autowired private PasswordEncoder passwordEncoder;

  private User1 testUser;

  @BeforeEach
  void setUp() {
    // Clear repository before each test
    userRepo.deleteAll();

    testUser = new User1();
    testUser.setUsername("integration_user");
    testUser.setPassword("password123");
    testUser.setRole("ROLE_TEACHER");
  }

  @Test
  void testSaveUserIntegration() {
    // Save user
    userService.save(testUser);

    // Verify user was saved
    User1 savedUser = userRepo.findByUsername("integration_user").orElse(null);

    assertNotNull(savedUser);
    assertEquals("integration_user", savedUser.getUsername());
    assertEquals("ROLE_TEACHER", savedUser.getRole());

    // Verify password was encoded
    assertNotEquals("password123", savedUser.getPassword());
    assertTrue(passwordEncoder.matches("password123", savedUser.getPassword()));
  }

  @Test
  void testLoadUserByUsernameIntegration_Success() {
    // First save the user
    userService.save(testUser);

    // Then load it
    UserDetails userDetails = userService.loadUserByUsername("integration_user");

    assertNotNull(userDetails);
    assertEquals("integration_user", userDetails.getUsername());
    assertTrue(passwordEncoder.matches("password123", userDetails.getPassword()));
    assertEquals(1, userDetails.getAuthorities().size());
    assertEquals("ROLE_TEACHER", userDetails.getAuthorities().iterator().next().getAuthority());
  }

  @Test
  void testLoadUserByUsernameIntegration_UserNotFound() {
    Exception exception =
        assertThrows(
            UsernameNotFoundException.class,
            () -> {
              userService.loadUserByUsername("nonexistent_user");
            });

    assertEquals("User not found", exception.getMessage());
  }

  @Test
  void testSaveMultipleUsers() {
    // Save first user
    testUser.setUsername("user1");
    userService.save(testUser);

    // Save second user
    User1 user2 = new User1();
    user2.setUsername("user2");
    user2.setPassword("pass2");
    user2.setRole("ROLE_STUDENT");
    userService.save(user2);

    // Verify both were saved
    assertTrue(userRepo.findByUsername("user1").isPresent());
    assertTrue(userRepo.findByUsername("user2").isPresent());

    // Verify passwords are encoded
    User1 savedUser1 = userRepo.findByUsername("user1").get();
    User1 savedUser2 = userRepo.findByUsername("user2").get();

    assertNotEquals("password123", savedUser1.getPassword());
    assertNotEquals("pass2", savedUser2.getPassword());
  }

  @Test
  void testPasswordEncodingConsistency() {
    userService.save(testUser);

    User1 savedUser = userRepo.findByUsername("integration_user").orElse(null);
    assertNotNull(savedUser);

    String encodedPassword1 = savedUser.getPassword();

    // Create another user with same password
    User1 anotherUser = new User1();
    anotherUser.setUsername("another_user");
    anotherUser.setPassword("password123"); // Same password
    anotherUser.setRole("ROLE_STUDENT");
    userService.save(anotherUser);

    User1 savedAnotherUser = userRepo.findByUsername("another_user").orElse(null);
    assertNotNull(savedAnotherUser);
    String encodedPassword2 = savedAnotherUser.getPassword();

    // Passwords should be encoded differently (due to salt)
    assertNotEquals(encodedPassword1, encodedPassword2);

    // But both should match the original password
    assertTrue(passwordEncoder.matches("password123", encodedPassword1));
    assertTrue(passwordEncoder.matches("password123", encodedPassword2));
  }

  @Test
  void testUpdateUser() {
    // Save user
    userService.save(testUser);

    // Update user
    User1 savedUser = userRepo.findByUsername("integration_user").get();
    savedUser.setRole("ROLE_STUDENT");
    userRepo.save(savedUser);

    // Verify update
    User1 updatedUser = userRepo.findByUsername("integration_user").get();
    assertEquals("ROLE_STUDENT", updatedUser.getRole());
  }
}
