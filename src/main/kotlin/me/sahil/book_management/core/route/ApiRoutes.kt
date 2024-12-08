package me.sahil.book_management.core.route
sealed class ApiRoutes {
    object AuthRoutes {
        const val PATH = "/api/auth"
        const val REGISTER = "/register"
        const val LOGIN = "/login"
        const val UPDATE_PASSWORD = "/update-password"
    }

    object UserRoutes {
        const val PATH = "/api/users"
        const val GET_ALL_USERS = ""
        const val GET_USER_BY_ID = "/{userId}"
        const val UPDATE_USER = "/{userId}"
        const val PATCH_USER = "/{userId}"
        const val DELETE_USER = "/{userId}"
        const val PROFILE = "/profile"
    }

    object FileRoutes {
        const val PATH = "/api/files"
        const val UPLOAD = "/upload"
        const val USER_FILES = "/user-files"
        const val DELETE_FILE = "/{fileId}"
        const val DOWNLOAD_FILE = "/download/{fileName}"
    }

    object BookRoutes {
        const val PATH = "/api/books"
        const val GET_ALL_BOOKS = ""
        const val ADD_BOOK = ""
        const val DELETE_BOOK = "/{bookId}"
        const val UPDATE_BOOK = "/{bookId}"
        const val PATCH_BOOK = "/{bookId}"
        const val GET_BOOK = "/{bookId}"
    }
}