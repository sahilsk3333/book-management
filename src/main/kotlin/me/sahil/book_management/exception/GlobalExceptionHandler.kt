package me.sahil.book_management.exception

import com.fasterxml.jackson.core.JsonProcessingException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val errors = ex.bindingResult.allErrors.map { (it as FieldError).defaultMessage }.joinToString(", ")
        val errorResponse = mapOf("error" to "Validation failed", "message" to errors)
        logger.error("Validation failed: $errors")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(JsonProcessingException::class)
    fun handleJsonParsingException(ex: JsonProcessingException): ResponseEntity<Map<String, String>> {
        logger.error("JSON parsing error: ${ex.message}")
        val errorResponse = mapOf("error" to "Bad Request", "message" to "Invalid request body. Please check your input fields.")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(e: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        logger.error("Bad request: ${e.message}")
        val errorResponse = mapOf("error" to "Invalid input: ${e.message ?: "Unknown error"}")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(e: Exception): ResponseEntity<Map<String, String>> {
        logger.error("Unexpected error: ${e.message}", e)
        val errorResponse = mapOf("error" to "Internal server error", "message" to (e.message ?: "Unknown error"))
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }

    @ExceptionHandler(TokenMissingException::class)
    fun handleTokenMissingException(ex: TokenMissingException): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(TokenExpiredException::class)
    fun handleTokenExpiredException(ex: TokenExpiredException): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(TokenInvalidException::class)
    fun handleTokenInvalidException(ex: TokenInvalidException): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.UNAUTHORIZED)
    }
}

