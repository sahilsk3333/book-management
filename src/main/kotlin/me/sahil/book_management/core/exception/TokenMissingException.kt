package me.sahil.book_management.core.exception

/**
 * Custom exception thrown when an authentication token is missing from the request.
 *
 * @param message the detail message explaining the reason for the exception.
 */
class TokenMissingException(message: String) : RuntimeException(message)


