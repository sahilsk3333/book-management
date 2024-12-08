package me.sahil.book_management.core.exception

import com.fasterxml.jackson.core.JsonProcessingException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val errors = ex.bindingResult.fieldErrors.associate { it.field to it.defaultMessage }
        logger.error("Validation failed: $errors")
        return ResponseEntity.badRequest().body(mapOf("error" to "Validation failed", "details" to errors))
    }

    // Handle Invalid JSON (Malformed JSON)
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleJsonParseException(exception: HttpMessageNotReadableException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(mapOf("error" to "Invalid JSON", "message" to "Request body is malformed"))
    }


    // Handle Validation Errors (missing or invalid fields)
    @ExceptionHandler(BindException::class)
    fun handleBindException(exception: BindException): ResponseEntity<Map<String, String>> {
        // Collect all field errors and include specific details
        val errorMessages =
            exception.bindingResult.fieldErrors.joinToString(", ") { "${it.field}: ${it.defaultMessage}" }

        // Return the response with the validation error messages
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(mapOf("error" to "Validation failed", "message" to errorMessages))
    }

    @ExceptionHandler(JsonProcessingException::class)
    fun handleJsonParsingException(ex: JsonProcessingException): ResponseEntity<Map<String, String>> {
        logger.error("JSON parsing error: ${ex.message}")
        val errorResponse =
            mapOf("error" to "Bad Request", "message" to "Invalid request body. Please check your input fields.")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(e: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        logger.error("Bad request: ${e.message}")
        val errorResponse = mapOf("error" to "Invalid input: ${e.message ?: "Unknown error"}")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(exception: NotFoundException): ResponseEntity<Map<Any, Any?>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to exception.message))
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
