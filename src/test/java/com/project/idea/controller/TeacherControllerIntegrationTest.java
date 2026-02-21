package com.project.idea.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.project.idea.model.Course1;
import com.project.idea.model.User1;
import com.project.idea.repository.CourseRepository;
import com.project.idea.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class TeacherControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private UserRepository userRepo;

  @Autowired private CourseRepository courseRepo;

  private User1 testStudent;
  private Course1 testCourse;

  @BeforeEach
  void setUp() {
    userRepo.deleteAll();
    courseRepo.deleteAll();

    // Create test student
    testStudent = new User1();
    testStudent.setUsername("test_student");
    testStudent.setPassword("password123");
    testStudent.setRole("ROLE_STUDENT");
    userRepo.save(testStudent);

    // Create test course
    testCourse = new Course1();
    testCourse.setCourseId("TEST101");
    testCourse.setCourseName("Test Course");
    testCourse.setCredit(3);
    courseRepo.save(testCourse);
  }

  @Test
  @WithMockUser(roles = "TEACHER") // Add this annotation
  void testTeacherDashboardIntegration() throws Exception {
    mockMvc
        .perform(get("/teacher"))
        .andExpect(status().isOk()) // Now expects 200 OK
        .andExpect(view().name("app"))
        .andExpect(model().attributeExists("page"))
        .andExpect(model().attribute("page", "teacher"))
        .andExpect(model().attributeExists("students"))
        .andExpect(model().attributeExists("courses"))
        .andExpect(model().attributeExists("newCourse"));
  }

  @Test
  @WithMockUser(roles = "TEACHER") // Add this annotation
  void testDeleteStudentIntegration() throws Exception {
    mockMvc
        .perform(post("/teacher/delete/{id}", testStudent.getId()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/teacher"));

    assertFalse(userRepo.findById(testStudent.getId()).isPresent());
  }

  @Test
  @WithMockUser(roles = "TEACHER") // Add this annotation
  void testAddCourseIntegration() throws Exception {
    mockMvc
        .perform(
            post("/teacher/add-course")
                .param("courseId", "NEW101")
                .param("courseName", "New Integration Course")
                .param("credit", "4"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/teacher"));

    assertTrue(courseRepo.findAll().stream().anyMatch(c -> c.getCourseId().equals("NEW101")));
  }
}
