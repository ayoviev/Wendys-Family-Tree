# Wendy's Family Tree

A web application for managing horses and their family tree / pedigree records.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 25 + Spring Boot 4.0 |
| Frontend | Angular 21 |
| Database | H2 2.4.x |
| Testing | JUnit 6 + AssertJ |
| Build | Maven 3 + NPM 11.9.0 |

## About

Individual assignment for the Software Engineering Project (SE PR) course at TU Wien, Summer Semester 2026.

The app allows users to create, view, search, and manage horses along with their ancestors through a REST API backend and an Angular frontend.

## Getting Started

### Backend

```bash
cd Wendys-Family-Tree/backend
./mvnw spring-boot:run
```

The backend runs on `http://localhost:8080`.

### Frontend

```bash
cd Wendys-Family-Tree/frontend
npm install
ng serve
```

The frontend runs on `http://localhost:4200`.
