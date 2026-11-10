# CodeArena

CodeArena is a backend RESTful platform built with **Spring Boot 3** and **PostgreSQL** for managing coding challenges, user authentication, and solution submissions.

> **Note on Project Status:** The core Problem management, User authentication (JWT + BCrypt), and Submission intake APIs are implemented and fully tested. Sandboxed code execution, automated judging engines, contests, and containerized deployment are planned for future phases (see [Roadmap](#future-roadmap)).

---

## Table of Contents
- [Overview](#overview)
- [Current Implemented Features](#current-implemented-features)
- [Technology Stack](#technology-stack)
- [Project Architecture & Directory Structure](#project-architecture--directory-structure)
- [Prerequisites](#prerequisites)
- [Local Setup & Installation](#local-setup--installation)
  - [1. Clone Repository](#1-clone-repository)
  - [2. PostgreSQL Database Setup](#2-postgresql-database-setup)
  - [3. Environment Variables Configuration](#3-environment-variables-configuration)
- [Running the Application](#running-the-application)
- [Running Automated Tests](#running-automated-tests)
- [API Endpoints Reference](#api-endpoints-reference)
- [Future Roadmap](#future-roadmap)
- [Security & Contribution Guidelines](#security--contribution-guidelines)

---

## Overview

CodeArena provides the foundational services needed for an online coding judge platform. It exposes RESTful APIs for:
- User registration, authentication, and role management.
- Comprehensive CRUD operations for algorithmic coding problems with test cases and examples.
- Code submission intake with ownership controls and status tracking.

---

## Current Implemented Features

### 1. User & Authentication Management
- **Registration & Login:** Endpoints for creating accounts and authenticating with credentials.
- **Stateless JWT Security:** Authentication via standard `Authorization: Bearer <token>` HTTP headers.
- **Password Security:** Password hashing using `BCryptPasswordEncoder`.
- **Role-Based Access Control (RBAC):** Distinct roles (`ROLE_USER`, `ROLE_ADMIN`) governing endpoint access.
- **Profile Endpoint:** Authenticated `/api/auth/me` to inspect the caller's profile.

### 2. Problem Management
- **Full CRUD for Admins:** Only administrators (`ROLE_ADMIN`) can create, update, or delete coding problems.
- **Public Browsing:** Unauthenticated users can view problem details and lists.
- **Search, Filter & Pagination:** Query problems by search term and difficulty (`EASY`, `MEDIUM`, `HARD`) with configurable pagination and sorting (`createdAt`, `difficulty`, `title`, `id`).
- **Entity Relationships:** One-to-many cascading relationships for problem examples and test cases.

### 3. Submission Intake
- **Authenticated Submission:** Logged-in users can submit solutions for problems across multiple languages (`JAVA`, `PYTHON`, `CPP`, `JAVASCRIPT`, `GO`, `RUST`).
- **Initial State Tracking:** Submissions are persisted with `PENDING` status, recording problem ID, user ID, language, source code, and submission timestamp.
- **Ownership Verification:** Users can only view their own submissions (`/api/submissions/my` and `/api/submissions/{id}`). Non-owner access is forbidden (`403 Forbidden`) unless requested by an administrator.

### 4. Robust Error Handling & Validation
- **RFC 7807 Problem Details:** Consistent JSON error structure across 400 (Bad Request / Validation errors), 403 (Forbidden / Access Denied), and 404 (Not Found).
- **Jakarta Bean Validation:** Comprehensive constraints on DTO fields (`@NotBlank`, `@NotNull`, `@Size`, etc.).

---

## Technology Stack

- **Language:** Java 17+
- **Framework:** Spring Boot 3.3.5
  - Spring Boot Starter Web (REST APIs)
  - Spring Boot Starter Data JPA (Hibernate ORM)
  - Spring Boot Starter Security (Spring Security 6)
  - Spring Boot Starter Validation (Hibernate Validator)
  - Spring Boot DevTools (Developer tooling)
- **Database:** PostgreSQL (production / local), H2 in-memory (automated test profile)
- **Token Security:** `jjwt` (Java JWT 0.12.6)
- **Build Tool:** Apache Maven
- **Testing:** JUnit 5, Mockito, AssertJ, Spring Security Test, Spring Boot Test (MockMvc)

---

## Project Architecture & Directory Structure

The project follows a standard layered architecture with clear separation of concerns:

```
codearena/
├── pom.xml                                      # Maven build descriptor and dependencies
├── .gitignore                                   # Git ignore rules for build, secrets, and IDE files
├── application.properties.example               # Root configuration example with safe placeholders
├── README.md                                    # Project documentation
├── src/
│   ├── main/
│   │   ├── java/com/codearena/
│   │   │   ├── CodeArenaApplication.java        # Spring Boot main entry point
│   │   │   ├── config/                          # Security & JWT filter configuration
│   │   │   │   ├── JwtAuthenticationFilter.java # Intercepts and validates Bearer tokens
│   │   │   │   └── SecurityConfig.java          # SecurityFilterChain and endpoint authorizations
│   │   │   ├── controller/                      # REST controllers (API endpoints)
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── ProblemController.java
│   │   │   │   └── SubmissionController.java
│   │   │   ├── dto/                             # Request and response data transfer objects
│   │   │   │   ├── AuthDTO.java
│   │   │   │   ├── ExampleDTO.java
│   │   │   │   ├── PageResponse.java
│   │   │   │   ├── ProblemDTO.java
│   │   │   │   ├── SubmissionDTO.java
│   │   │   │   └── TestCaseDTO.java
│   │   │   ├── entity/                          # JPA database entities
│   │   │   │   ├── Difficulty.java              # Enum: EASY, MEDIUM, HARD
│   │   │   │   ├── Example.java
│   │   │   │   ├── Language.java                # Enum: JAVA, PYTHON, CPP, JAVASCRIPT, GO, RUST
│   │   │   │   ├── Problem.java
│   │   │   │   ├── Role.java                    # Enum: USER, ADMIN
│   │   │   │   ├── Submission.java
│   │   │   │   ├── SubmissionStatus.java        # Enum: PENDING, IN_QUEUE, ACCEPTED, etc.
│   │   │   │   ├── TestCase.java
│   │   │   │   └── User.java
│   │   │   ├── exception/                       # Custom exceptions & global RFC 7807 handler
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── repository/                      # Spring Data JPA repositories
│   │   │   │   ├── ProblemRepository.java
│   │   │   │   ├── ProblemSpecification.java
│   │   │   │   ├── SubmissionRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   └── service/                         # Business logic interfaces and implementations
│   │   │       ├── JwtService.java
│   │   │       ├── ProblemMapper.java
│   │   │       ├── ProblemService.java
│   │   │       ├── ProblemServiceImpl.java
│   │   │       ├── SubmissionService.java
│   │   │       ├── SubmissionServiceImpl.java
│   │   │       ├── UserService.java
│   │   │       └── UserServiceImpl.java
│   │   └── resources/
│   │       ├── application.properties           # Application configuration reading env variables
│   │       └── application.properties.example   # Example template with configuration instructions
│   └── test/
│       ├── java/com/codearena/                  # Unit and integration test suite
│       │   ├── SecurityIntegrationTest.java     # End-to-end integration tests with H2
│       │   ├── config/JwtAuthenticationFilterTest.java
│       │   ├── controller/
│       │   │   ├── AuthControllerTest.java
│       │   │   ├── ProblemControllerTest.java
│       │   │   └── SubmissionControllerTest.java
│       │   ├── repository/ProblemRepositoryTest.java
│       │   └── service/
│       │       ├── ProblemServiceImplTest.java
│       │       ├── SubmissionServiceImplTest.java
│       │       └── UserServiceImplTest.java
│       └── resources/
│           └── application-test.properties      # Test profile properties (in-memory H2)
```

---

## Prerequisites

- **Java Development Kit (JDK):** Version 17 or higher
- **Apache Maven:** Version 3.8+ (or Maven wrapper)
- **PostgreSQL Database:** Version 14 or higher running locally or accessible via network

---

## Local Setup & Installation

### 1. Clone Repository
```bash
git clone https://github.com/<your-username>/codearena.git
cd codearena
```

### 2. PostgreSQL Database Setup
Ensure PostgreSQL service is running and create the database:
```sql
CREATE DATABASE codearena;
```

Verify you have user credentials (e.g., `postgres`) with read/write access to `codearena`.

### 3. Environment Variables Configuration

CodeArena reads its sensitive credentials and environment-specific settings from environment variables.

| Environment Variable | Description | Default Value | Required? |
|----------------------|-------------|---------------|-----------|
| `DB_URL` | PostgreSQL JDBC connection URL | `jdbc:postgresql://localhost:5432/codearena` | No |
| `DB_USERNAME` | PostgreSQL database username | `postgres` | No |
| `DB_PASSWORD` | PostgreSQL database password | *(empty)* | **Yes** (in production/local) |
| `JWT_SECRET` | 256-bit (>= 32 chars) HMAC secret key | *(empty)* | **Yes** |
| `JWT_EXPIRATION_MINUTES` | Token lifetime in minutes | `60` | No |

#### Setting Environment Variables

**Windows (PowerShell):**
```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/codearena"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your_postgres_password"
$env:JWT_SECRET="your-secure-jwt-secret-key-at-least-32-characters"
$env:JWT_EXPIRATION_MINUTES="60"
```

**Windows (Command Prompt):**
```cmd
set DB_URL=jdbc:postgresql://localhost:5432/codearena
set DB_USERNAME=postgres
set DB_PASSWORD=your_postgres_password
set JWT_SECRET=your-secure-jwt-secret-key-at-least-32-characters
set JWT_EXPIRATION_MINUTES=60
```

**Linux / macOS (Bash / Zsh):**
```bash
export DB_URL="jdbc:postgresql://localhost:5432/codearena"
export DB_USERNAME="postgres"
export DB_PASSWORD="your_postgres_password"
export JWT_SECRET="your-secure-jwt-secret-key-at-least-32-characters"
export JWT_EXPIRATION_MINUTES="60"
```

> **Generating a Secure JWT Secret:**
> - Linux/macOS: `openssl rand -base64 32`
> - Windows PowerShell: `[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Minimum 0 -Maximum 256 }))`

---

## Running the Application

Once environment variables are configured and PostgreSQL is running:

```bash
mvn spring-boot:run
```

The application will start on port `8080` by default (`http://localhost:8080`).

You can also package into an executable JAR:
```bash
mvn clean package
java -jar target/codearena-0.0.1-SNAPSHOT.jar
```

---

## Running Automated Tests

The test suite runs against an isolated in-memory **H2 database** (configured in `src/test/resources/application-test.properties`). No external PostgreSQL instance or environment variables are required to run tests.

Run all unit and integration tests:
```bash
mvn test
```

Run full build and verification:
```bash
mvn clean verify
```

---

## API Endpoints Reference

### Authentication (`/api/auth`)

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `POST` | `/api/auth/register` | Public | Register a new user (`username`, `email`, `password`) |
| `POST` | `/api/auth/login` | Public | Authenticate user and receive JWT Bearer token |
| `GET` | `/api/auth/me` | Authenticated | Retrieve current user profile (`id`, `username`, `email`, `role`) |

### Problems (`/api/problems`)

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `GET` | `/api/problems` | Public | List problems with search, difficulty filter, pagination, and sorting |
| `GET` | `/api/problems/{id}` | Public | Retrieve problem details by ID |
| `POST` | `/api/problems` | Admin (`ROLE_ADMIN`) | Create a new coding problem with test cases/examples |
| `PUT` | `/api/problems/{id}` | Admin (`ROLE_ADMIN`) | Update an existing coding problem |
| `DELETE` | `/api/problems/{id}` | Admin (`ROLE_ADMIN`) | Delete a problem by ID |

#### Query Parameters for `GET /api/problems`:
- `search` (string, optional): Search by problem title or description substring.
- `difficulty` (string, optional): `EASY`, `MEDIUM`, or `HARD`.
- `page` (integer, default: 0): Page index.
- `size` (integer, default: 10): Page size (max 100).
- `sortBy` (string, default: `createdAt`): Sort field (`createdAt`, `difficulty`, `title`, `id`).
- `sortDir` (string, default: `desc`): Sort direction (`asc` or `desc`).

### Submissions (`/api/submissions`)

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `POST` | `/api/submissions` | Authenticated | Submit code solution (`problemId`, `language`, `sourceCode`) |
| `GET` | `/api/submissions/my` | Authenticated | Retrieve current user's submissions ordered by time |
| `GET` | `/api/submissions/{id}` | Authenticated (Owner / Admin) | Retrieve a specific submission by ID |

---

## Future Roadmap

The following features are **planned** and will be introduced in subsequent phases:

- [ ] **Sandboxed Code Execution Engine:** Isolated execution container (e.g., Docker / gVisor / Firecracker) to securely compile and execute user-submitted code within defined time and memory limits.
- [ ] **Asynchronous Judging Queue:** Integration with a message broker (RabbitMQ / Apache Kafka / Redis Streams) to decouple submission ingestion from execution workers.
- [ ] **Automated Test Evaluator:** Test runner comparing submission stdout against hidden test cases with verdicts (`ACCEPTED`, `WRONG_ANSWER`, `TIME_LIMIT_EXCEEDED`, `RUNTIME_ERROR`, etc.).
- [ ] **Live Verdicts via WebSockets:** Real-time push updates to clients as submissions move through `IN_QUEUE`, `PROCESSING`, and final status.
- [ ] **Contest & Leaderboard System:** Timed contest participation, score calculations, penalty times, and real-time rank tables.
- [ ] **Containerization & CI/CD:** Official `Dockerfile`, `docker-compose.yml`, and GitHub Actions workflow for automated testing and builds.

---

## Security & Contribution Guidelines

- **Never commit `.env` or files containing plaintext credentials.**
- Always verify changes against `mvn clean verify` before creating pull requests.
- Use strong, randomly generated keys for production `JWT_SECRET`.

