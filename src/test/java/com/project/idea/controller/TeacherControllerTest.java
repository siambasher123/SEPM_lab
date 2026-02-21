package com.project.idea.controller;

import com.project.idea.model.Course1;
import com.project.idea.model.User1;
import com.project.idea.repository.CourseRepository;
import com.project.idea.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TeacherControllerTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private CourseRepository courseRepo;

    @Mock
    private Model model;

    @InjectMocks
    private TeacherController teacherController;

    private User1 student;
    private Course1 course;

    @BeforeEach
    void setUp() {
        // Create test student
        student = new User1();
        student.setId(1L);
        student.setUsername("john_doe");
        student.setPassword("password");
        student.setRole("ROLE_STUDENT");

        // Create test course
        course = new Course1();
        course.setId(1L);
        course.setCourseId("CS101");
        course.setCourseName("Java Programming");
        course.setCredit(3);
    }

    @Test
    void testTeacherDashboard() {
        // Mock data
        List<User1> students = Arrays.asList(student);
        List<Course1> courses = Arrays.asList(course);

        // Mock repository calls
        when(userRepo.findByRole("ROLE_STUDENT")).thenReturn(students);
        when(courseRepo.findAll()).thenReturn(courses);

        // Call the method
        String viewName = teacherController.teacherDashboard(model);

        // Verify results
        assertEquals("app", viewName);

        // Verify model interactions
        verify(model).addAttribute("page", "teacher");
        verify(model).addAttribute("students", students);
        verify(model).addAttribute("courses", courses);
        verify(model).addAttribute(eq("newCourse"), any(Course1.class));
    }

    @Test
    void testDeleteStudent() {
        Long studentId = 1L;

        String result = teacherController.deleteStudent(studentId);

        verify(userRepo).deleteById(studentId);
        assertEquals("redirect:/teacher", result);
    }

    @Test
    void testAddCourse() {
        String result = teacherController.addCourse(course);

        verify(courseRepo).save(course);
        assertEquals("redirect:/teacher", result);
    }
}