## Overview

The Loan Pricing & Deal Approval System is a full-stack corporate banking application designed to digitize and standardize the loan request, pricing, and approval workflow used by banks for mid-market and enterprise clients.

The system replaces spreadsheet-driven and email-based workflows with a secure, role-based, auditable platform that enables relationship managers to create loan requests and credit managers to review, approve, or reject them.

This project is implemented as part of a capstone full-stack application using Angular, Spring Boot, MongoDB, JWT authentication, and Docker-ready architecture.

-----

## Technology Stack
   ### Frontend
     Angular 18 (Standalone components)
     TypeScript
     HTML / CSS
     RxJS
     Angular Router
     HTTP Interceptors
     Route Guards (Auth & Role based)
     Vitest for unit testing

   ### Backend
     Spring Boot 3.x
     Java 17+
     Spring Security
     JWT Authentication
     Spring Data MongoDB
     Bean Validation
     JUnit + Mockito

   ### Database
     MongoDB
     
   ### Architecture & Tools
     RESTful API
     Role-Based Access Control (RBAC)
     DTO + Service + Repository pattern
     Global Exception Handling
     Docker-ready configuration

-----

## System Roles
   ### USER (Relationship Manager)
    Create loan requests (DRAFT)
    Edit non-sensitive fields while in DRAFT
    Perform basic pricing calculation   
    Submit loans for approval
    View loan list and loan details

   ### ADMIN (Credit Manager)
    View all loans
    Review submitted loans
    Approve or reject loan requests
    Edit sensitive fields (sanctioned amount, approved interest rate)
    Manage users (activate / deactivate)
    View complete audit history

-----

## Loan Workflow
   ### Loan Statuses
    DRAFT
    SUBMITTED
    UNDER_REVIEW
    APPROVED
    REJECTED

   ### Allowed Transitions
    USER: DRAFT → SUBMITTED
    ADMIN:
        SUBMITTED → UNDER_REVIEW
        UNDER_REVIEW → APPROVED
        UNDER_REVIEW → REJECTED
All status changes are audited and persisted.

-----

## Frontend Application Structure (Angular 18)
src/
 └── app/
     ├── admin/
     │   └── manage-users/
     ├── auth/
     │   └── login/
     ├── core/
     │   ├── guards/
     │   │   ├── auth-guard.ts
     │   │   └── role-guard.ts
     │   ├── interceptors/
     │   │   └── auth-interceptor.ts
     │   └── services/
     │       ├── auth.service.ts
     │       └── loan.service.ts
     ├── dashboard/
     ├── loans/
     │   ├── apply-loan/
     │   └── loan-list/
     ├── models/
     │   ├── loan.model.ts
     │   ├── user.model.ts
     │   ├── financials.model.ts
     │   └── page-response.model.ts
     ├── app.routes.ts
     ├── app.config.ts
     └── main.ts

## Key Frontend Features
    JWT token stored securely in localStorage
    HTTP Interceptor injects Authorization header
    Role-based route protection
    UI dynamically adapts based on role and loan status
    Editing disabled once loan is submitted
    ADMIN-only approval panel
    Vitest-based unit tests

-----

## Backend Application Structure (Spring Boot)
    src/main/java/com/java/spr/
    ├── config/
    │   ├── SecurityConfig.java
    │   └── PasswordConfig.java
    ├── controller/
    │   ├── AuthController.java
    │   ├── LoanController.java
    │   ├── UserController.java
    │   └── AdminController.java
    ├── dto/
    │   ├── LoginRequest.java
    │   ├── LoginResponse.java
    │   ├── LoanRequest.java
    │   ├── LoanResponse.java
    │   └── UpdateUserStatusRequest.java
    ├── exception/
    │   ├── ApiErrorResponse.java
    │   └── GlobalExceptionHandler.java
    ├── mapper/
    │   └── LoanMapper.java
    ├── model/
    │   ├── Loan.java
    │   ├── User.java
    │   ├── Financials.java
    │   ├── LoanAction.java
    │   └── enums/
    │       ├── LoanStatus.java
    │       └── Role.java
    ├── repository/
    │   ├── LoanRepository.java
    │   └── UserRepository.java
    ├── security/
    │   ├── JwtAuthenticationFilter.java
    │   └── JwtUtil.java
    ├── service/
    │   ├── LoanService.java
    │   └── CustomUserDetailsService.java
    └── LoanApprovalCapstoneApplication.java

-----

## API Endpoints

   ### Authentication & Users
-------------------------------------------------------------------------------------------
| Method | Endpoint                       | Description                    | Role         |
| ------ | ------------------------------ | ------------------------------ | ------------ |
| POST   | `/api/auth/login`              | Authenticate user & return JWT | Public       |
| GET    | `/api/users/me`                | Get logged-in user             | USER / ADMIN |
| GET    | `/api/admin/users`             | List users                     | ADMIN        |
| POST   | `/api/admin/users`             | Create user                    | ADMIN        |
| PUT    | `/api/admin/users/{id}/status` | Activate / Deactivate          | ADMIN        |
-------------------------------------------------------------------------------------------

   ### Loans
--------------------------------------------------------------------------------
| Method | Endpoint                 | Description               | Role         |
| ------ | ------------------------ | ------------------------- | ------------ |
| POST   | `/api/loans`             | Create loan               | USER / ADMIN |
| GET    | `/api/loans`             | List loans (pagination)   | USER / ADMIN |
| GET    | `/api/loans/{id}`        | Loan details              | USER / ADMIN |
| PUT    | `/api/loans/{id}`        | Update loan (DRAFT only)  | USER         |
| PATCH  | `/api/loans/{id}/status` | Submit / Approve / Reject | USER / ADMIN |
| DELETE | `/api/loans/{id}`        | Soft delete               | ADMIN        |
--------------------------------------------------------------------------------

-----

## MongoDB Configuration
    spring.data.mongodb.uri=mongodb://localhost:27017/loan_db

   ### Sample Loan Document
    {
    "clientName": "OmniTech Pvt Ltd",
    "loanType": "TermLoan",
    "requestedAmount": 5000000,
    "tenureMonths": 36,
    "financials": {
        "revenue": 120000000,
        "ebitda": 14000000,
        "rating": "A"
    },
    "status": "SUBMITTED",
    "actions": [
        {
        "action": "SUBMITTED",
        "comments": "Documents verified",
        "timestamp": "2026-01-08T10:15:00"
        }
    ]
    }

-----

## Security
    JWT-based authentication
    BCrypt password hashing
    Stateless session management
    Role-based authorization
    Secure route protection (frontend + backend)

-----

## Exception Handling
    All API errors follow a standardized format:
    {
        "timestamp": "ISODate",
        "status": 400,
        "error": "Bad Request"
    }

-----

## Testing
   ## Backend
    JUnit + Mockito
    Controller tests
    Service tests

   ### Frontend
    Vitest
    Service tests
    Guard tests
    Component tests




Author

Shaili
Full-Stack Developer
Angular | Spring Boot | MongoDB | JWT | Docker


