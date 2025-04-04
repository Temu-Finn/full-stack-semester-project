package edu.ntnu.idatt2105.gr2.backend.exception

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.security.SignatureException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authentication.AccountStatusException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException
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
     * Handles UserAlreadyExistsException and converts it to a 409 Conflict response.
     * @param ex The UserAlreadyExistsException that was thrown
     * @return A ProblemDetail containing the error information
     */
    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExistsException(ex: UserAlreadyExistsException): ProblemDetail {
        logger.warn("Attempt to create an already existing user", ex)
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message)
            .apply { setProperty("description", "A user with the provided email already exists.") }
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

    /**
     * Handles malformed JSON requests and converts them to a 400 Bad Request response.
     * @param ex The HttpMessageNotReadableException that was thrown
     * @return A ProblemDetail containing the error information
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException): ProblemDetail {
        logger.warn("Malformed JSON request received", ex)
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Malformed JSON request")
            .apply { setProperty("description", "The request body is not valid JSON") }
    }

    /**
     * Handles unsupported HTTP methods and converts them to a 405 Method Not Allowed response.
     * @param ex The HttpRequestMethodNotSupportedException that was thrown
     * @return A ProblemDetail containing the error information
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupportedException(ex: HttpRequestMethodNotSupportedException): ProblemDetail {
        logger.warn("Unsupported HTTP method requested", ex)
        return ProblemDetail.forStatusAndDetail(HttpStatus.METHOD_NOT_ALLOWED, ex.message)
            .apply { setProperty("description", "The HTTP method is not supported for this endpoint") }
    }

    /**
     * Handles 404 Not Found errors and converts them to a 404 response.
     * @param ex The NoHandlerFoundException that was thrown
     * @return A ProblemDetail containing the error information
     */
    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(ex: NoHandlerFoundException): ProblemDetail {
        logger.warn("No handler found for request", ex)
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Endpoint not found")
            .apply { setProperty("description", "The requested endpoint does not exist") }
    }

    /**
     * Handles missing required parameters and converts them to a 400 Bad Request response.
     * @param ex The MissingServletRequestParameterException that was thrown
     * @return A ProblemDetail containing the error information
     */
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameterException(ex: MissingServletRequestParameterException): ProblemDetail {
        logger.warn("Missing required parameter", ex)
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Missing required parameter")
            .apply { 
                setProperty("description", "The required parameter '${ex.parameterName}' of type '${ex.parameterType}' is missing")
            }
    }
}
