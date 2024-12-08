package me.sahil.book_management.core.exception

/**
 * Custom exception thrown when an authentication token has expired.
 *
 * @param message the detail message explaining the reason for the exception.
 */
class TokenExpiredException(message: String) : RuntimeException(message)