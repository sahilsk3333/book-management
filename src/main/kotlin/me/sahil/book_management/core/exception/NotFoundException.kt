package me.sahil.book_management.core.exception

/**
 * Custom exception thrown when a requested resource is not found.
 *
 * @param message the detail message that explains why this exception was thrown.
 */
class NotFoundException(message: String) : RuntimeException(message)