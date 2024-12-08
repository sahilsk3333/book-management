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


/**
 * Global exception handler for managing various exceptions in the application.
 * This class ensures that all exceptions are handled centrally and appropriate error responses are returned to the client.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    /**
     * Handles validation errors when request parameters or body are invalid.
     * @param ex the exception thrown due to invalid method arguments
     * @return a response entity containing the validation error details and status 400
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val errors = ex.bindingResult.fieldErrors.associate { it.field to it.defaultMessage }
        logger.error("Validation failed: $errors") // Log validation failure
        return ResponseEntity.badRequest().body(mapOf("error" to "Validation failed", "details" to errors))
    }

    /**
     * Handles errors related to malformed JSON in the request body.
     * @param exception the exception thrown when the JSON body is malformed
     * @return a response entity with a 400 status and error details
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleJsonParseException(exception: HttpMessageNotReadableException): ResponseEntity<Map<String, String>> {
        logger.error("Malformed JSON in request: ${exception.message}") // Log JSON parsing error
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(mapOf("error" to "Invalid JSON", "message" to "Request body is malformed"))
    }

    /**
     * Handles binding errors when the request body cannot be mapped to an object.
     * @param exception the exception thrown when data binding fails
     * @return a response entity with a 400 status and specific error details
     */
    @ExceptionHandler(BindException::class)
    fun handleBindException(exception: BindException): ResponseEntity<Map<String, String>> {
        // Collect all field errors and include specific details
        val errorMessages =
            exception.bindingResult.fieldErrors.joinToString(", ") { "${it.field}: ${it.defaultMessage}" }

        // Log the binding errors
        logger.error("Binding error: $errorMessages")
        // Return the response with the validation error messages
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(mapOf("error" to "Validation failed", "message" to errorMessages))
    }

    /**
     * Handles errors that occur during JSON parsing.
     * @param ex the exception thrown during JSON parsing
     * @return a response entity with a 400 status and error details
     */
    @ExceptionHandler(JsonProcessingException::class)
    fun handleJsonParsingException(ex: JsonProcessingException): ResponseEntity<Map<String, String>> {
        logger.error("JSON parsing error: ${ex.message}") // Log JSON parsing error
        val errorResponse =
            mapOf("error" to "Bad Request", "message" to "Invalid request body. Please check your input fields.")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    /**
     * Handles bad request errors such as invalid arguments passed by the client.
     * @param e the exception thrown due to invalid input
     * @return a response entity with a 400 status and the error message
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(e: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        logger.error("Bad request: ${e.message}") // Log bad request details
        val errorResponse = mapOf("error" to "Invalid input: ${e.message ?: "Unknown error"}")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    /**
     * Handles not found errors when a requested resource does not exist.
     * @param exception the exception thrown when a resource is not found
     * @return a response entity with a 404 status and the error message
     */
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(exception: NotFoundException): ResponseEntity<Map<Any, Any?>> {
        logger.error("Resource not found: ${exception.message}") // Log resource not found error
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to exception.message))
    }

    /**
     * Handles general unexpected exceptions.
     * @param e the exception thrown due to unexpected errors
     * @return a response entity with a 500 status and generic error message
     */
    @ExceptionHandler(Exception::class)
    fun handleGeneralException(e: Exception): ResponseEntity<Map<String, String>> {
        logger.error("Unexpected error: ${e.message}", e) // Log unexpected error details
        val errorResponse = mapOf("error" to "Internal server error", "message" to (e.message ?: "Unknown error"))
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }

    /**
     * Handles token-related exceptions when the token is missing.
     * @param ex the exception thrown due to missing token
     * @return a response entity with a 401 status and the exception message
     */
    @ExceptionHandler(TokenMissingException::class)
    fun handleTokenMissingException(ex: TokenMissingException): ResponseEntity<String> {
        logger.error("Token missing: ${ex.message}") // Log missing token error
        return ResponseEntity(ex.message, HttpStatus.UNAUTHORIZED)
    }

    /**
     * Handles token expiration errors when the token is no longer valid.
     * @param ex the exception thrown due to expired token
     * @return a response entity with a 401 status and the exception message
     */
    @ExceptionHandler(TokenExpiredException::class)
    fun handleTokenExpiredException(ex: TokenExpiredException): ResponseEntity<String> {
        logger.error("Token expired: ${ex.message}") // Log expired token error
        return ResponseEntity(ex.message, HttpStatus.UNAUTHORIZED)
    }

    /**
     * Handles invalid token errors when the provided token is not valid.
     * @param ex the exception thrown due to invalid token
     * @return a response entity with a 401 status and the exception message
     */
    @ExceptionHandler(TokenInvalidException::class)
    fun handleTokenInvalidException(ex: TokenInvalidException): ResponseEntity<String> {
        logger.error("Token invalid: ${ex.message}") // Log invalid token error
        return ResponseEntity(ex.message, HttpStatus.UNAUTHORIZED)
    }


    /**
     * Handles access denial errors when a user tries to access a resource without the proper permissions.
     * @param ex the exception thrown when a user does not have the required access
     * @return a response entity with a 403 status and the error message
     */
    @ExceptionHandler(IllegalAccessException::class)
    fun handleIllegalAccessException(ex: IllegalAccessException): ResponseEntity<Map<String, String>> {
        logger.error("Access denied: ${ex.message}") // Log access denial error
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(mapOf("error" to "Forbidden", "message" to (ex.message ?: "Access denied")))
    }

}

