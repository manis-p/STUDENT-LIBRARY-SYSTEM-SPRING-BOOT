"Student Library System is a web application that provides core features of library management. It includes user registration, login, profile management, password reset (via OTP and email link), and implements secure authentication and authorization using Spring Security and JWT."
Features
User Signup with validation

User Login with JWT-based authentication

View and Update User Profile

Password Change using old password

Forgot Password feature with Email OTP and Reset Link

Password Reset via OTP and token validation

Role-based Authorization (User/Admin roles)

Secure API endpoints with Spring Security and JWT
Technology Stack & Tools
Java 17

Spring Boot (REST APIs)

Spring Security with JWT (JSON Web Token)

Maven (Build tool)

Spring Data JPA (Database interaction)

MySQL (or any preferred relational DB)

Email sending with JavaMailSender

Lombok (optional, for boilerplate code reduction)

IDE: VS Code / IntelliJ IDEA

Project Structure & Package Explanation
Package	Description
com.librarysystem.config	Spring Security configurations — Authentication & Authorization setup
com.librarysystem.otp	OTP generation, storage, and validation logic
com.librarysystem.security.filter	JWT filter that intercepts requests to validate tokens and set security context
com.librarysystem.util	Utility classes — JWT token generation and validation helpers
com.librarysystem.service	Business logic — signup, login, password reset/change, OTP handling, email sending
com.librarysystem.controller	REST Controllers to handle HTTP requests for authentication, user profile, password reset etc.
com.librarysystem.email	Email services for sending OTP and password reset links
com.librarysystem.model	JPA Entity classes mapping database tables (User, Roles, etc.)
com.librarysystem.repository	Spring Data repositories for database CRUD operations
com.librarysystem.dto	Data Transfer Objects for request and response payloads

THIS IS MY APPLICATION.PROPERTIES FILE
# Spring Datasource Configuration
spring.datasource.name=student
spring.application.name=StudentLibrarySystem
spring.datasource.url=jdbc:mysql://localhost:3306/studentlibrary
spring.datasource.username=root
spring.datasource.password=7493831815Mj
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.use_sql_comments=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update


# Enable pretty print of the SQL queries
spring.jpa.properties.hibernate.format_sql=true


spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=jham7340@gmail.com
spring.mail.password=jwgurmwzdummdbfq
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.connectiontimeout=5000
spring.mail.properties.mail.smtp.timeout=5000
spring.mail.properties.mail.smtp.writetimeout=5000
logging.level.org.springframework.mail=DEBUG
logging.level.org.apache.commons.mail=DEBUG
Build and run the project using Maven:
mvn clean install
mvn spring-boot:run
Application will start at http://localhost:8080
API Endpoints
POST /api/user/signup - Register new user

POST /api/user/login - Login and get JWT token

GET /api/user/profile - Get logged-in user profile (secured)

PUT /api/user/update-profile - Update user profile (secured)

POST /api/user/change-password - Change password (secured)

POST /api/user/forgot-password - Send OTP and reset link to email

POST /api/user/reset-password?token=xyz - Reset password with OTP and token
NOTE:
JWT tokens are used to secure APIs and must be passed in Authorization header as Bearer <token>.

Password reset tokens expire in 5 minutes for security.

OTPs are stored temporarily in memory and validated with expiry logic.

Spring Security roles manage access to sensitive endpoints.

** This project is under active development. More features will be added soon**
