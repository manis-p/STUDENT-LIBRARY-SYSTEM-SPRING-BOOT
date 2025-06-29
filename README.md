# 📚 Student Library System

**Student Library System** is a full-featured web application that handles user authentication, profile management, and secure access using **Spring Security with JWT** and **2-Factor Authentication (2FA)** via email OTP. It supports **multi-device login management**, **refresh token handling**, and **secure logout with blacklist-based token invalidation**.

---

## ✅ Features

* 🔐 **User Signup**
* 🔐 **Login with 2FA (OTP via Email) + Spring Security JWT**
* 👤 **Get User Profile**
* ✏️ **Update Profile**
* ❌ **Soft Delete Profile** – Marks user as deleted with a message if trying to login again
* 🔁 **Change Password**

  * Using Old Password
  * Using Email OTP
* 📧 **Forgot Password**

  * Sends Email OTP and Reset Link via JavaMailSender
* 🔄 **Access + Refresh Token Authentication**

  * Refresh token used to generate new access token
  * Blacklist active access token on logout
  * Tokens auto-removed from DB after expiry (for memory optimization)
* 📱 **Device-Level Session Management**

  * Single-device logout
  * Specific device logout
* ⏳ **Token Expiry Tracking + Auto Cleanup**

---

## 🔗 API Endpoints

| Method | Endpoint         | Description                                 |
| ------ | ---------------- | ------------------------------------------- |
| POST   | /signup          | User registration                           |
| POST   | /login           | User login with JWT + 2FA                   |
| GET    | /profile         | Get user profile (secured)                  |
| PUT    | /update          | Update user profile                         |
| DELETE | /delete          | Soft delete user profile                    |
| POST   | /verify-otp      | 2FA OTP verification                        |
| POST   | /logout          | Logout from current device                  |
| POST   | /logout-device   | Logout from specific device                 |
| POST   | /refresh         | Generate new access token via refresh token |
| POST   | /change-password | Change password using old password          |
| POST   | /forgot-password | Send OTP & reset link to email              |
| POST   | /reset-password  | Reset password using email and token        |

---

## 🧰 Package Structure

Main base package: com.librarysystem

| Package Name    | Purpose                                                          |
| --------------- | ---------------------------------------------------------------- |
| config          | Spring Security configuration (JWT setup, filters, auth rules)   |
| controller      | REST Controllers: AuthController, SessionController, UserHandler |
| dto             | DTOs for request & response data                                 |
| email           | Email-related logic (sending OTP, reset links)                   |
| exception       | Custom exception handling (global and local)                     |
| model           | Entity classes (e.g., User)                                      |
| otp             | OTP generation, validation, and expiry                           |
| repository      | Spring Data JPA repositories (DB interaction)                    |
| filter          | JWT filter – intercepts and validates every request              |
| securityservice | Spring Security logic (UserDetailsService, token handling)       |
| service         | Business logic: signup, login, password reset/change             |
| util            | Utility classes (JWT utility, token generation, email helper)    |

---

## 💻 Tech Stack

| Layer                | Technology                                                |
| -------------------- | --------------------------------------------------------- |
| 👨‍💻 Language          | Java 17                                                   |
| ⚙️ Backend Framework | Spring Boot (REST APIs)                                   |
| 🔐 Security          | Spring Security + JWT + 2FA OTP                           |
| 🗃️ Database          | MySQL + Spring Data JPA                                   |
| ✉️ Email Services    | JavaMailSender (Gmail SMTP)                               |
| 🔄 Token Handling    | Access Token + Refresh Token (Blacklist + Expiry Cleanup) |
| 📬 API Documentation | Swagger UI (OpenAPI 3)                                    |
| 📬 API Testing       | Postman                                                   |
| 🧠 Build Tool        | Maven                                                     |
| ✏️ Code Reduction    | Lombok (optional)                                         |
| 💻 IDEs              | IntelliJ IDEA / Eclipse / VS Code                         |

---

## 🔐 Security & Token Flow

* **JWT** is used for securing endpoints.
* **2FA** is implemented using email OTP during login.
* **Access Token + Refresh Token**:

  * Refresh token helps generate new access token.
  * Both tokens are stored and cleaned up after expiry.
  * On logout, access token is blacklisted, refresh token is removed.
* **Soft Delete Logic**:

  * When a user is deleted, login shows a friendly error message.
* **Multi-device support**:

  * Each login gets a unique token.
  * Logout from a specific device or all devices is supported.

---

## 🦪 How to Run

```bash
# Step 1: Build the project
mvn clean install

# Step 2: Run the application
mvn spring-boot:run
```

➡️ App will run at: `http://localhost:8080`

---

## 🔍 Swagger UI Preview

The project includes **integrated Swagger UI** for easy API exploration and testing.

### 1. 🔐 Login Endpoint (2FA OTP)

After submitting email/password, OTP is sent to your email.
This ensures 2-Factor Authentication during login.

---

### 2. ✅ Verify OTP & Get JWT Tokens

On submitting correct OTP, system returns:

* 🔑 AccessToken
* 🔁 RefreshToken

These tokens are used to access secure endpoints and manage sessions.

---

### 3. 📋 API Documentation Overview

The Swagger UI shows:

* Grouped endpoints (AuthController, SessionController, etc.)
* Proper HTTP methods (GET, POST, PUT, DELETE)
* Real-time testing with input fields

---

### 🔐 Token Authorization on Swagger

Once you have the accessToken, click on **"Authorize"** in top-right of Swagger and enter:

```bash
Bearer your_access_token_here
```

Now you can test secured APIs like /profile, /update, /logout, etc.

---

## 👨‍💼 Developer Info

* 👤 Name: Manish Jha
* 📧 Email: [jham7340@gmail.com]
* 🌍 Location: India

---

## ⚙️ `application.properties` Configuration

```properties
# --------------------------------------
# Spring Datasource Configuration
# --------------------------------------
spring.datasource.name=student
spring.application.name=StudentLibrarySystem
spring.datasource.url=jdbc:mysql://localhost:3306/studentlibrary
spring.datasource.username=root
spring.datasource.password=7493831815Mj
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# --------------------------------------
# JPA / Hibernate Configuration
# --------------------------------------
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.use_sql_comments=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.format_sql=true
spring.profiles.active=default

# --------------------------------------
# Email Configuration (JavaMailSender)
# -------------------------------------
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=jmak7340@gmail.com
spring.mail.password=jwgurmwzdummd
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.connectiontimeout=5000
spring.mail.properties.mail.smtp.timeout=5000
spring.mail.properties.mail.smtp.writetimeout=5000

# --------------------------------------
# Mail Debug Logs
# --------------------------------------
logging.level.org.springframework.mail=DEBUG
logging.level.org.apache.commons.mail=DEBUG
```



🚀 More features like **book module**, **admin dashboard**, and **borrowing history** coming soon!




