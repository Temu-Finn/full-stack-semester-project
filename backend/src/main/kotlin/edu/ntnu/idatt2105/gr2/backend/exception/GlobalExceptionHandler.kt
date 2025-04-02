package edu.ntnu.idatt2105.gr2.backend.exception

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.security.SignatureException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.security.authentication.AccountStatusException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.slf4j.LoggerFactory

/**
 * Global exception handler for the application that handles various types of exceptions
 * and converts them to appropriate HTTP responses using Spring's ProblemDetail.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    /**
     * Handles security-related exceptions and converts them to appropriate HTTP responses.
     * @param exception The exception that was thrown
     * @return A ProblemDetail containing the error information
     */
    @ExceptionHandler(Exception::class)
    fun handleSecurityException(exception: Exception): ProblemDetail {
        logger.error("Security exception occurred", exception)
        
        return when (exception) {
            is BadCredentialsException -> ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.message)
                .apply { setProperty("description", "The username or password is incorrect") }
            
            is AccountStatusException -> ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.message)
                .apply { setProperty("description", "The account is locked") }
            
            is AccessDeniedException -> ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.message)
                .apply { setProperty("description", "You are not authorized to access this resource") }
            
            is SignatureException -> ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.message)
                .apply { setProperty("description", "The JWT signature is invalid") }
            
            is ExpiredJwtException -> ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.message)
                .apply { setProperty("description", "The JWT token has expired") }
            
            else -> ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.message)
                .apply { setProperty("description", "Unknown internal server error") }
        }
    }

    /**
     * Handles IllegalArgumentException and converts it to a 400 Bad Request response.
     * @param ex The IllegalArgumentException that was thrown
     * @return A ProblemDetail containing the error information
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ProblemDetail {
        logger.warn("Illegal argument exception occurred", ex)
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message)
    }

    /**
     * Handles IllegalStateException and converts it to a 400 Bad Request response.
     * @param ex The IllegalStateException that was thrown
     * @return A ProblemDetail containing the error information
     */
    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(ex: IllegalStateException): ProblemDetail {
        logger.warn("Illegal state exception occurred", ex)
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message)
    }

    /**
     * Handles validation exceptions and converts them to a 400 Bad Request response.
     * @param ex The MethodArgumentNotValidException that was thrown
     * @return A ProblemDetail containing the error information
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ProblemDetail {
        logger.warn("Validation exception occurred", ex)
        val errorMessage = ex.bindingResult.allErrors
            .mapNotNull { it.defaultMessage }
            .joinToString(", ")
        
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errorMessage)
    }
}
