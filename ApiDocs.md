# Book Management System API Documentation

This documentation describes the API endpoints for a Book Management System. The system supports three roles: **ADMIN**, **AUTHOR**, and **READER**, each with specific permissions.

## Authentication

Authenticated endpoints require authentication using a Bearer token. Include the token in the `Authorization` header:

```
Authorization: Bearer <TOKEN>
```

---

## Endpoints

### Authentication

#### Login
- **URL:** `/api/auth/login`
- **Method:** `POST`
- **Description:** Log in to obtain a token.
- **Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```
- **Response:**
```json
{
  "token": "<JWT_TOKEN>",
  "user": {}
}
```

#### Register
- **URL:** `/api/auth/register`
- **Method:** `POST`
- **Description:** Register a new user.
- **Request Body:**
```json
{
  "name": "John Doe",
  "email": "john.doe2@example.com",
  "password": "password123",
  "age": 25,
  "image": "url_to_image",
  "role": "AUTHOR"
}
```
- **Response:**
```json
{
  "message": "User registered successfully!",
  "user": {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe2@example.com",
    "age": 25,
    "image": null,
    "role": "AUTHOR"
  }
}
```

#### Update Password
- **URL:** `/api/auth/update-password`
- **Method:** `PATCH`
- **Description:** Update the user's password.
- **Request Body:**
```json
{
  "currentPassword": "password123",
  "newPassword": "newPassword123"
}
```
- **Response:**
```json
{
  "message": "Password updated successfully."
}
```

---

### Books

#### Get All Books
- **URL:** `/api/books`
- **Method:** `GET`
- **Description:** Retrieve all books (paginated).
- **Roles:** ADMIN, AUTHOR, READER
- **Response:**
```json
{
  "content": [
    {
      "id": 1,
      "author": {
        "id": 1,
        "name": "John Doe",
        "email": "john.doe2@example.com",
        "age": 25,
        "image": null,
        "role": "AUTHOR"
      },
      "name": "New Book Title",
      "description": "Description of the new book",
      "pdfUrl": "http://example.com/newbook.pdf",
      "isbn": "978-3-16-108410-4",
      "createdAt": "2024-12-12T15:30:38.991312Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 1,
    "sort": {
      "empty": true,
      "unsorted": true,
      "sorted": false
    },
    "offset": 0,
    "unpaged": false,
    "paged": true
  },
  "last": true,
  "totalPages": 1,
  "totalElements": 1,
  "first": true,
  "size": 1,
  "number": 0,
  "sort": {
    "empty": true,
    "unsorted": true,
    "sorted": false
  },
  "numberOfElements": 1,
  "empty": false
}

```

#### Get Book by ID
- **URL:** `/api/books/{id}`
- **Method:** `GET`
- **Description:** Retrieve details of a specific book by its ID.
- **Roles:** ADMIN, READER
- **Response:**
```json
{
  "id": 3,
  "author": {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe2@example.com",
    "age": 25,
    "image": null,
    "role": "AUTHOR"
  },
  "name": "Hello",
  "description": "Description of the new book",
  "pdfUrl": "asd",
  "isbn": "978-3-16-148410-4",
  "createdAt": "2024-12-12T15:30:46.988632Z"
}
```

#### Create Book
- **URL:** `/api/books`
- **Method:** `POST`
- **Description:** Create a new book.
- **Roles:** AUTHOR
- **Request Body:**
```json
{
  "name": "New Book Title",
  "description": "Description of the new book",
  "pdfUrl": "http://example.com/newbook.pdf",
  "isbn" : "978-3-16-108410-5"
}

```
- **Response:**
```json
{
  "id": 1,
  "name": "New Book Title",
  "description": "Description of the new book",
  "pdfUrl": "http://example.com/newbook.pdf",
  "isbn": "978-3-16-108410-5",
  "createdAt": "2024-12-13T14:37:43.626297047Z"
}
```

#### Update Book
- **URL:** `/api/books/{id}`
- **Method:** `PUT`
- **Description:** Update an existing book.
- **Roles:** AUTHOR (own books)
- **Request Body:**
```json
{
  "name": "Hello",
  "description": "Description of the new book",
  "isbn": "978-3-16-148410-4",
  "pdfUrl": "https://dummypdfurl.com/as.pdf"
}
```
- **Response:**
```json
{
  "id": 3,
  "author": {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe2@example.com",
    "age": 25,
    "image": null,
    "role": "AUTHOR"
  },
  "name": "Hello",
  "description": "Description of the new book",
  "pdfUrl": "https://dummypdfurl.com/as.pdf",
  "isbn": "978-3-16-148410-4",
  "createdAt": "2024-12-12T15:30:46.988632Z"
}
```

#### Patch Book
- **URL:** `/api/books/{id}`
- **Method:** `PATCH`
- **Description:** Update an existing book.
- **Roles:** AUTHOR (own books)
- **Request Body:**
```json
{
  "pdfUrl": "https://dummypdfurl.com/as.pdf"
}
```
- **Response:**
```json
{
  "id": 3,
  "author": {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe2@example.com",
    "age": 25,
    "image": null,
    "role": "AUTHOR"
  },
  "name": "Hello",
  "description": "Description of the new book",
  "pdfUrl": "https://dummypdfurl.com/as.pdf",
  "isbn": "978-3-16-148410-4",
  "createdAt": "2024-12-12T15:30:46.988632Z"
}
```

#### Delete Book
- **URL:** `/api/books/{id}`
- **Method:** `DELETE`
- **Description:** Delete a book.
- **Roles:** AUTHOR (own books)
- **Response:**
```json
{
  "message": "Book with ID {id} has been deleted."
}
```

---

### Users

#### Get All Users
- **URL:** `/api/users`
- **Method:** `GET`
- **Description:** Retrieve all users (paginated).
- **Roles:** ADMIN
- **Response:**
```json
{
  "content": [
    {
      "id": 1,
      "name": "John Doe",
      "email": "john.doe@example.com",
      "role": "AUTHOR"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalPages": 1
}
```

#### Get User by ID
- **URL:** `/api/users/{id}`
- **Method:** `GET`
- **Description:** Retrieve a user's details by their ID.
- **Roles:** ADMIN
- **Response:**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "role": "AUTHOR"
}
```

#### Delete User
- **URL:** `/api/users/{id}`
- **Method:** `DELETE`
- **Description:** Delete a user by their ID.
- **Roles:** ADMIN
- **Response:**
```json
{
  "message": "User deleted successfully."
}
```

#### Get Own Profile
- **URL:** `/api/users/profile`
- **Method:** `GET`
- **Description:** Retrieve the authenticated user's profile.
- **Roles:** ADMIN, AUTHOR, READER
- **Response:**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "role": "AUTHOR"
}
```


---

### Files

#### Upload File
- **URL:** `/api/files/upload`
- **Method:** `POST`
- **Description:** Upload a file.
- **Roles:** ADMIN, AUTHOR, READER
- **Request Body:**
  (Multipart Form-Data)
- **Response:**
```json
{
  "message": "File uploaded successfully",
  "file": {
    "id": 1,
    "fileName": "f3fd38f6-485b-4a35-9ddc-c518abc08f4b-dummy.pdf",
    "mimeType": "application/pdf",
    "downloadUrl": "https://book-management-uwna.onrender.com/api/files/download/f3fd38f6-485b-4a35-9ddc-c518abc08f4b-dummy.pdf",
    "createdAt": "2024-12-14T05:12:21.988014835Z"
  }
}
```

#### Get Uploaded Files
- **URL:** `/api/files`
- **Method:** `GET`
- **Description:** Retrieve the authenticated user's uploaded files.
- **Roles:** ADMIN, AUTHOR, READER
- **Response:**
```json
[
  {
    "id": 1,
    "fileName": "f3fd38f6-485b-4a35-9ddc-c518abc08f4b-dummy.pdf",
    "mimeType": "application/pdf",
    "downloadUrl": "https://book-management-uwna.onrender.com/api/files/download/f3fd38f6-485b-4a35-9ddc-c518abc08f4b-dummy.pdf",
    "createdAt": "2024-12-14T05:12:21.988015Z"
  }
]
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
