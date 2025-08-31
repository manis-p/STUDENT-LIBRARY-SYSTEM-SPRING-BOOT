
# 📚 Student Library System

**Student Library System** – An End-to-End **Backend Application** built with **Spring Boot**, featuring **User & Admin Panels**, **Secure Authentication (Spring Security + JWT)**, **Role-Based Access**, **Multi-Device Login Management**, **Refresh Tokens**, **Blacklist-Based Logout**, and planned **Payment System Integration**.

---

## ✅ Features

### 🚀 Current Progress

I am building the **backend** of a complete Student Library System. So far, the following modules are implemented:

#### 👤 User Panel

* **Signup** – Register new users with validation.
* **Login with 2FA** – Email OTP + JWT-based authentication using Spring Security.
* **Get Profile** – Fetch user details securely.
* **Update Profile** – Modify user information.
* **Soft Delete Profile** – Marks user as deleted and prevents login with the same account.
* **Change Password** – Securely update password after authentication.

#### 🛠️ Admin Panel

* **Admin Authentication** – Secure login with Spring Security & JWT.
* **Manage Users** – View, update, or soft delete users.
* **Role-Based Access** – Access is restricted using roles (`USER`, `ADMIN`).

#### 🧪 Additional Features

* **Unit Testing** – Using `JUnit` & `Mockito` for service layer testing.
* **Custom Validation & Exception Handling** – Includes validation via DTO annotations (`@NotBlank`, `@Email`, `@Pattern`, etc.) and global exception handling.
* **Forgot Password** – Sends email OTP & reset link via JavaMailSender.
* **Access + Refresh Token Authentication** – Refresh token used to generate new access token; blacklist active access tokens on logout; tokens auto-removed from DB after expiry.
* **Device-Level Session Management** – Single-device logout, specific device logout.
* **Token Expiry Tracking + Auto Cleanup** – Tokens removed automatically after expiry for memory optimization.

---

## 🛡️ Validation, Exception Handling & Unit Testing

### ✅ Validation

* Strict input validation using DTO annotations (`@NotBlank`, `@Email`, `@Pattern`, `@Size`)
* Ensures clean and valid data before hitting service layer.

### ❗ Exception Handling

#### 📦 Custom Exceptions (`exception` package):

* `UserNotFoundException`
* `OtpExpiredException`
* `InvalidTokenException`
* `AlreadyDeletedException`
* And more...

#### 🌐 Global Exception Handler:

* Centralized handling via `@ControllerAdvice`.
* Handles field validation errors, custom exceptions, and unexpected server errors.
* Sends clean error responses to frontend.

### 🧪 Unit Testing

* Service layer tested using `JUnit 5` + `Mockito`.
* Tests located in the `unit` package.
* Ensures business logic correctness and system reliability.

---

## 🔗 API Endpoints

### **1️⃣ `api/user`**

| Method | Endpoint      | Description                                      |
| ------ | ------------- | ------------------------------------------------ |
| POST   | /signup       | Register a new user                              |
| POST   | /signup/admin | Register a new admin                             |
| POST   | /login        | User login with credentials                      |
| GET    | /user/profile | Fetch logged-in user's profile                   |
| PUT    | /user/update  | Update logged-in user's profile                  |
| DELETE | /delete/{id}  | Soft delete user profile by ID                   |
| POST   | /verify-otp   | Verify 2FA OTP during login or sensitive actions |

### **2️⃣ `api/session`**

| Method | Endpoint          | Description                                     |
| ------ | ----------------- | ----------------------------------------------- |
| POST   | /logoutuser       | Logout from the current device                  |
| POST   | /logoutAllDevices | Logout from all devices associated with user    |
| POST   | /refresh          | Generate a new access token using refresh token |

### **3️⃣ `api/auth`**

| Method | Endpoint         | Description                                             |
| ------ | ---------------- | ------------------------------------------------------- |
| POST   | /change-password | Change password using old password for authorized users |
| POST   | /forgot-password | Send OTP & reset link to registered email               |
| POST   | /reset-password  | Reset password by verifying OTP or token                |

### **4️⃣ `api/admin`**

| Method | Endpoint                           | Description                              |
| ------ | ---------------------------------- | ---------------------------------------- |
| GET    | /single/users/{id}                 | Get details of a single user             |
| DELETE | /delete/users/{id}                 | Delete a single user                     |
| DELETE | /delete/multiple/users             | Delete multiple users                    |
| PUT    | /update/users/{id}                 | Update user information                  |
| POST   | /admin/users/{id}/role/change      | Change role of a single user             |
| POST   | /admin/users/roles/change/multiple | Change roles of multiple users           |
| GET    | /admin/users/search                | Search users by name or email            |
| GET    | /filterby/role/{role}              | Filter users by role                     |
| GET    | /all/users                         | Get all users                            |
| GET    | /loginhistory/{userId}             | Get login history of a user              |
| GET    | /unique-login-users-count          | Get unique count of logged-in users      |
| POST   | /force-send-reset-link/{userId}    | Force send password reset link to a user |

---

## 🧰 Package Structure

| Package Name    | Purpose                                                                           |
| --------------- | --------------------------------------------------------------------------------- |
| config          | Spring Security configuration (JWT setup, filters, auth rules)                    |
| controller      | REST Controllers: AuthController, SessionController, UserHandler, AdminController |
| dto             | DTOs for request & response data                                                  |
| email           | Email-related logic (sending OTP, reset links)                                    |
| exception       | Custom exception handling (global and local)                                      |
| model           | Entity classes (e.g., User)                                                       |
| otp             | OTP generation, validation, and expiry                                            |
| repository      | Spring Data JPA repositories (DB interaction)                                     |
| filter          | JWT filter – intercepts and validates every request                               |
| securityservice | Spring Security logic (UserDetailsService, token handling)                        |
| service         | Business logic: signup, login, password reset/change                              |
| util            | Utility classes (JWT utility, token generation, email helper)                     |

---

## 💻 Tech Stack

| Layer                | Technology                                                |
| -------------------- | --------------------------------------------------------- |
| 👨‍💻 Language       | Java 17                                                   |
| ⚙️ Backend Framework | Spring Boot (REST APIs)                                   |
| 🔐 Security          | Spring Security + JWT + 2FA OTP                           |
| 🗃️ Database         | MySQL + Spring Data JPA                                   |
| ✉️ Email Services    | JavaMailSender (Gmail SMTP)                               |
| 🔄 Token Handling    | Access Token + Refresh Token (Blacklist + Expiry Cleanup) |
| 📬 API Documentation | Swagger UI (OpenAPI 3)                                    |
| 📬 API Testing       | Postman                                                   |
| 🧠 Build Tool        | Maven                                                     |
| ✏️ Code Reduction    | Lombok (optional)                                         |
| 💻 IDEs              | IntelliJ IDEA / Eclipse / VS Code                         |

---

## 🔐 Security & Token Flow

* **JWT** secures endpoints.
* **2FA** via email OTP during login.
* **Access Token + Refresh Token**:

  * Refresh token generates new access token.
  * Tokens stored and cleaned after expiry.
  * Logout blacklists access token and removes refresh token.
* **Soft Delete Logic**:

  * Deleted users see a friendly error on login.
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

➡️ App runs at: `http://localhost:8080`

---

## 🔍 Swagger UI Preview

### 1. 🔐 Login Endpoint (2FA OTP)

After submitting email/password, OTP is sent to email to ensure 2FA login.

### 2. ✅ Verify OTP & Get JWT Tokens

Returns **AccessToken** and **RefreshToken** for secure access.

### 3. 📋 API Documentation Overview

Swagger UI shows grouped endpoints, HTTP methods, and allows real-time testing.

**Token Authorization on Swagger:**
Enter your token under **"Authorize"**:

```bash
Bearer your_access_token_here
```

Test secured APIs like `/profile`, `/update`, `/logout`, etc.

---

## 👨‍💼 Developer Info

* Name: Manish Jha
* Email: [jham7340@gmail.com](mailto:jham7340@gmail.com)
* Location: India

---

## ⚙️ `application.properties` Configuration

```properties
# Datasource
spring.datasource.url=jdbc:mysql://localhost:3306/studentlibrary
spring.datasource.username=root
spring.datasource.password=7493831815Mj
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA / Hibernate
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Email (JavaMailSender)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=jmak7340@gmail.com
spring.mail.password=jwgurmwzdummd
spring.mail.properties.mail.smtp.auth=true
spring.mail

🚀 More features like **book module**, **admin dashboard**, and **borrowing history** coming soon! 



