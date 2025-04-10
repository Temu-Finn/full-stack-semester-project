package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.dto.UserResponse
import edu.ntnu.idatt2105.gr2.backend.exception.UserAlreadyExistsException
import edu.ntnu.idatt2105.gr2.backend.model.User
import edu.ntnu.idatt2105.gr2.backend.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val userRepository: UserRepository,
    private val authenticationManager: AuthenticationManager,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
) {
    fun signup(
        name: String,
        email: String,
        password: String,
    ): UserResponse {
        if (userRepository.findByEmail(email) != null) {
            throw UserAlreadyExistsException("User with email $email already exists")
        }

        val userToSave = User(name = name, email = email, passwordHashed = passwordEncoder.encode(password))

        return userRepository.save(userToSave).toResponse()
    }

    fun authenticate(
        email: String,
        password: String,
    ): UserResponse {
        if (email.isBlank() || password.isBlank()) {
            throw IllegalArgumentException("Email and password cannot be blank")
        }

        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                email,
                password,
            ),
        )

        return userRepository.findByEmail(email)?.toResponse()
            ?: throw IllegalStateException("User $email authenticated but not found in repository.")
    }

    fun User.toResponse(): UserResponse {
        val token = jwtService.generateToken(this)
        return UserResponse(
            userId = id,
            name = name,
            email = email,
            joinedAt = joinedAt,
            isAdmin = isAdmin,
            token = token,
            expiresIn = jwtService.getExpirationTime(),
        )
    }
}
