package me.sahil.book_management.core.utils

import me.sahil.book_management.core.exception.TokenInvalidException

/**
 * Extension function for a nullable [String] that extracts the Bearer token from the Authorization header.
 *
 * This function checks if the string is not null or blank and if it starts with the "Bearer " prefix.
 * If either condition fails, it throws a [TokenInvalidException] with an appropriate message.
 * If the conditions are met, it removes the "Bearer " prefix and returns the trimmed token.
 *
 * @return The extracted Bearer token as a [String].
 * @throws TokenInvalidException If the Authorization header is invalid or missing.
 */
fun String?.extractBearerToken(): String {
    if (this.isNullOrBlank() || !this.startsWith("Bearer ")) {
        throw TokenInvalidException("Invalid or missing Authorization header")
    }
    return this.removePrefix("Bearer ").trim()
}
