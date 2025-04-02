package edu.ntnu.idatt2105.gr2.backend.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "User already exists")
class UserAlreadyExistsException(message: String) : RuntimeException(message) 