# Book Management System

This repository contains a Spring Boot-based Book Management System. The project is designed to manage books and authors, providing CRUD operations, authentication, and secure data handling using JWT. The project also includes Docker support for containerization.

## Features

- Role-based access control (ADMIN, AUTHOR, READER).
- Manage books and authors.
- Secure authentication with JWT.
- PostgreSQL database integration.
- Dockerized for easy deployment.
- Comprehensive API documentation in `ApiDocs.md`.

---

## Getting Started

### Prerequisites

To run this project, ensure you have the following installed:

- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)
- [Java 17](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)
- [Gradle](https://gradle.org/)

---

### Setup

#### 1. Clone the Repository

```bash
git clone https://github.com/sahilsk3333/book-management.git
cd book-management
```

#### 2. Set Up `.env` File

Create a `.env` file in the project root directory with the following configuration:

```
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/postgres
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_postgres_password

# JWT Secret Key Configuration
JWT_SECRET_KEY=your_jwt_secret_key

# PostgreSQL Configuration
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_postgres_password

# Base URL Configuration
SERVER_BASE_URL=http://127.0.0.1:8080
```

Replace `your_postgres_password` and `your_jwt_secret_key` with your custom values.

---

#### 3. Build and Run the Project

Use Docker Compose to build and run the application:

```bash
docker-compose up --build
```

This command will:

- Build the application using the `Dockerfile`.
- Spin up two services:
    1. `app`: The Spring Boot application container.
    2. `db`: The PostgreSQL database container.

The application will be available at `http://127.0.0.1:8080`.

---

## Project Structure

```
book-management/
├── src/                # Source code
├── ApiDocs.md          # API documentation
├── Dockerfile          # Docker build configuration
├── docker-compose.yml  # Docker Compose configuration
├── build.gradle        # Gradle build file
├── application.properties  # Spring Boot configuration (environment-based)
├── .env.example        # Example .env file
```

---

## Role Permissions

### ADMIN
- Manage all users (CRUD).
- Access and manage all books.
- Manage own profile and uploaded files.

### AUTHOR
- Add, update, and delete own books.
- View all books.
- Manage own profile and uploaded files.

### READER
- View all books.
- View details of specific books.
- Manage own profile and uploaded files.

---

## API Documentation

Comprehensive API documentation is available in the `ApiDocs.md` file located in the root directory.

---


## Postman Integration

For testing and exploring the API, Postman collection and environment files are provided in the `postman/` directory.

### Steps to Import Postman Files
1. Open Postman and navigate to the **Import** option.
2. Import the following files from the `postman/` directory:
  - **Collection**: `book-management.postman_collection.json`
  - **Environment**: `book-management.postman_environment.json`
3. Set the environment to `book-management` in Postman.

The Postman collection includes all API endpoints with pre-configured request parameters and headers. The environment file contains the necessary variables like `baseUrl` and `jwtToken` for seamless testing.

You can also test the live APIs using the base URL: https://book-management-uwna.onrender.com.

---

## Useful Commands

### Start the Application

```bash
docker-compose up
```

### Stop the Application

```bash
docker-compose down
```

### Rebuild the Application

```bash
docker-compose up --build
```

---

## Notes

1. The `.env` file is not included in the repository for security reasons. Users must create their own `.env` file as described above.
2. Ensure that the `JWT_SECRET_KEY` value is secure and not exposed.
3. The `ApiDocs.md` file provides all the necessary details about the API endpoints.

---

## Contact

For any questions or issues, please contact [Sahil Khan](mailto:sahil.alwar.sk@gmail.com).

