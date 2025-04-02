package edu.ntnu.idatt2105.gr2.backend.exception

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.security.SignatureException
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.security.authentication.AccountStatusException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleSecurityException(exception: Exception): ProblemDetail {
        var errorDetail: ProblemDetail? = null

        // TODO send this stack trace to an observability tool
        exception.printStackTrace()

        if (exception is BadCredentialsException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exception.message)
            errorDetail.setProperty("description", "The username or password is incorrect")
        }

        if (exception is AccountStatusException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.message)
            errorDetail.setProperty("description", "The account is locked")
        }

        if (exception is AccessDeniedException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.message)
            errorDetail.setProperty("description", "You are not authorized to access this resource")
        }

        if (exception is SignatureException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.message)
            errorDetail.setProperty("description", "The JWT signature is invalid")
        }

        if (exception is ExpiredJwtException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.message)
            errorDetail.setProperty("description", "The JWT token has expired")
        }

        if (errorDetail == null) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), exception.message)
            errorDetail.setProperty("description", "Unknown internal server error.")
        }

        return errorDetail
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), ex.message)
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(ex: IllegalStateException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), ex.message)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ProblemDetail {
        var errorMessage = "";
        ex.bindingResult.allErrors.forEach { error ->
            if (errorMessage.isNotEmpty()) {
                errorMessage += ", "
            }
            errorMessage += error.defaultMessage
        }

        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), errorMessage)
    }
}
