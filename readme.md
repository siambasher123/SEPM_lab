# Course Management System

A Spring Boot project that provides a comprehensive course management platform. The system supports two roles: Teacher and Student, each with specific permissions and functionalities.

## Table of Contents
- [Project Description](#project-description)
- [Architecture](#architecture)
- [ER Diagram](#er-diagram)
- [Features](#features)
- [API Endpoints](#api-endpoints)
- [Tech Stack](#tech-stack)
- [Run Instructions](#run-instructions)
- [License](#license)

## Project Description

Course Management System is a web-based application that allows:

- **Teachers** → Can create and manage courses, view enrolled students, manage course materials
- **Students** → Can browse available courses, enroll in courses, view their course schedule

The application is developed with Spring Boot, Java, and Maven, and uses PostgreSQL for data persistence. Additionally, it features Spring Security for authentication and authorization, session-based enrollment management, and comprehensive role-based access control.

## Architecture

The project follows a standard Spring Boot layered architecture:

- **Controller Layer** – Handles HTTP requests and routes them to services
- **Service Layer** – Implements business logic
- **Repository Layer** – Interacts with the database
- **Model Layer** – Represents entities and data structures

### Architecture Components:
```
┌─────────────────────────────────────────┐
│         Web Browser / Client             │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│      Spring Security (Auth Layer)        │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│        Controller Layer                  │
│  (AuthController, StudentController,    │
│   TeacherController)                    │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│        Service Layer                    │
│  (UserService, CourseService)           │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│     Repository Layer (JPA)              │
│  (UserRepository, CourseRepository)     │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│      Database (PostgreSQL)              │
└─────────────────────────────────────────┘
```

## ER Diagram

The database structure consists of the following key entities:

- **User1** – Represents Teacher and Student with role-based access
- **Course1** – Courses created by Teachers, enrolled by Students

### Entity Relationships:
```
┌─────────────────────────┐
│       User1             │
├─────────────────────────┤
│ id (PK)                 │
│ username                │
│ email                   │
│ password                │
│ role (ROLE_TEACHER/     │
│       ROLE_STUDENT)     │
└─────────────────────────┘

┌─────────────────────────┐
│      Course1            │
├─────────────────────────┤
│ id (PK)                 │
│ courseId                │
│ courseName              │
│ credit                  │
└─────────────────────────┘
```

## Features

### Authentication & Authorization
- User signup/login with role-based access control
- Spring Security integration with BCrypt password encoding
- Session-based user authentication
- Automatic redirection based on user role

### Teacher Features
- Dashboard for managing courses
- Create new courses with course ID and credits
- View all enrolled students
- Delete student records from the system

### Student Features
- Dashboard for browsing available courses
- View all available courses in the system
- Enroll in courses (add to selected courses)
- Unenroll from courses (remove from selected courses)
- View personal course schedule
- Session-based enrollment management

## API Endpoints

### Authentication Endpoints
| Method | Path | Auth | Role | Description |
|--------|------|------|------|-------------|
| GET | `/login` | Public | All | Show login page |
| GET | `/signup` | Public | All | Show signup page |
| POST | `/signup` | Public | All | Register a new user (student or teacher) |
| GET | `/dashboard` | Required | All | Redirect user to dashboard based on role |

### Teacher Endpoints
| Method | Path | Auth | Role | Description |
|--------|------|------|------|-------------|
| GET | `/teacher` | Required | TEACHER | View teacher dashboard with all students and courses |
| POST | `/teacher/add-course` | Required | TEACHER | Create a new course |
| POST | `/teacher/delete/{id}` | Required | TEACHER | Delete a student by ID |

### Student Endpoints
| Method | Path | Auth | Role | Description |
|--------|------|------|------|-------------|
| GET | `/student` | Required | STUDENT | View student dashboard with available and selected courses |
| GET | `/student/my-courses` | Required | STUDENT | View enrolled courses |
| GET | `/student/available-courses` | Required | STUDENT | View all available courses to enroll |
| POST | `/student/add-course` | Required | STUDENT | Enroll in a course by course ID |
| POST | `/student/remove-course` | Required | STUDENT | Unenroll from a course by course ID |

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Security | Spring Security, BCrypt |
| Database | PostgreSQL (runtime), H2 (tests) |
| ORM | Spring Data JPA / Hibernate |
| Views | Thymeleaf + Spring Security Extras |
| Testing | JUnit 5, Mockito |
| Session Management | HttpSession |
| Build Tool | Maven |

## Run Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+

### Clone the Repository
```bash
git clone https://github.com/siambasher123/SEPM_lab.git
cd SEPM_lab
```

### Configure Database
Update `application.properties` or `application.yml` with your PostgreSQL credentials:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/sepm_lab
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

### Build the Project
```bash
mvn clean install
```

### Run the Application
```bash
mvn spring-boot:run
```

The application will start at: **http://localhost:8080**

### Default Login Credentials
- **Teacher Account**
  - Username: `teacher1`
  - Password: `password123`
  - Role: `ROLE_TEACHER`

- **Student Account**
  - Username: `student1`
  - Password: `password123`
  - Role: `ROLE_STUDENT`

## Project Structure
```
SEPM_lab/
├── src/main/java/com/project/idea/
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── StudentController.java
│   │   └── TeacherController.java
│   ├── model/
│   │   ├── User1.java
│   │   └── Course1.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   └── CourseRepository.java
│   ├── service/
│   │   └── UserService.java
│   └── SEPMLab.java (Main Application)
├── src/main/resources/
│   ├── templates/
│   │   └── app.html
│   └── application.properties
├── pom.xml
└── readme.md
```

## User Roles & Permissions

### Teacher (ROLE_TEACHER)
- ✅ Access teacher dashboard
- ✅ Create new courses
- ✅ View all students
- ✅ Delete student accounts
- ❌ Cannot enroll in courses

### Student (ROLE_STUDENT)
- ✅ Access student dashboard
- ✅ Browse all courses
- ✅ Enroll in courses
- ✅ Unenroll from courses
- ✅ View personal schedule
- ❌ Cannot create or delete courses

## License

This project is part of the Software Engineering Project Management (SEPM) coursework.