package me.sahil.book_management.core.utils

import me.sahil.book_management.core.exception.TokenInvalidException

fun String?.extractBearerToken(): String {
    if (this.isNullOrBlank() || !this.startsWith("Bearer ")) {
        throw TokenInvalidException("Invalid or missing Authorization header")
    }
    return this.removePrefix("Bearer ").trim()
}
