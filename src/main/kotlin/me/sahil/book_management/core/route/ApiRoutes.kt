package me.sahil.book_management.core.route

/**
 * A sealed class that defines all the API routes for the application.
 * Each route is organized into nested objects representing different API categories.
 */
sealed class ApiRoutes {

    companion object {
        /**
         * A list of routes that do not require authentication.
         */
        val UNAUTHENTICATED_ROUTES = arrayOf(
            AuthRoutes.PATH + AuthRoutes.REGISTER,
            AuthRoutes.PATH + AuthRoutes.LOGIN,
            FileRoutes.PATH + "/download/**"
        )
    }

    /**
     * Contains routes related to authentication, such as login and registration.
     */
    object AuthRoutes {
        /** Base path for authentication routes. */
        const val PATH = "/api/auth"

        /** Route for user registration. */
        const val REGISTER = "/register"

        /** Route for user login. */
        const val LOGIN = "/login"

        /** Route for updating the user's password. */
        const val UPDATE_PASSWORD = "/update-password"
    }

    /**
     * Contains routes related to user management, such as retrieving and updating user information.
     */
    object UserRoutes {
        /** Base path for user routes. */
        const val PATH = "/api/users"

        /** Route for retrieving all users. */
        const val GET_ALL_USERS = ""

        /** Route for retrieving a user by ID. */
        const val GET_USER_BY_ID = "/{userId}"

        /** Route for updating user details. */
        const val UPDATE_USER = ""

        /** Route for partially updating user details. */
        const val PATCH_USER = ""

        /** Route for deleting a user by ID. */
        const val DELETE_USER = "/{userId}"

        /** Route for retrieving the authenticated user's profile. */
        const val PROFILE = "/profile"
    }

    /**
     * Contains routes related to file management, such as uploading and downloading files.
     */
    object FileRoutes {
        /** Base path for file routes. */
        const val PATH = "/api/files"

        /** Route for uploading files. */
        const val UPLOAD = "/upload"

        /** Route for retrieving user-specific files. */
        const val USER_FILES = "/user-files"

        /** Route for deleting a file by ID. */
        const val DELETE_FILE = "/{fileId}"

        /** Route for downloading a file by name. */
        const val DOWNLOAD_FILE = "/download/{fileName}"
    }

    /**
     * Contains routes related to book management, such as adding, updating, and deleting books.
     */
    object BookRoutes {
        /** Base path for book routes. */
        const val PATH = "/api/books"

        /** Route for retrieving all books. */
        const val GET_ALL_BOOKS = ""

        /** Route for adding a new book. */
        const val ADD_BOOK = ""

        /** Route for deleting a book by ID. */
        const val DELETE_BOOK = "/{bookId}"

        /** Route for updating a book by ID. */
        const val UPDATE_BOOK = "/{bookId}"

        /** Route for partially updating a book by ID. */
        const val PATCH_BOOK = "/{bookId}"

        /** Route for retrieving a book by ID. */
        const val GET_BOOK = "/{bookId}"
    }
}
