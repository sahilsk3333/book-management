package me.sahil.book_management.core.exception

/**
 * Custom exception thrown when an authentication token is invalid.
 *
 * @param message the detail message explaining the reason for the exception.
 */
class TokenInvalidException(message: String) : RuntimeException(message)