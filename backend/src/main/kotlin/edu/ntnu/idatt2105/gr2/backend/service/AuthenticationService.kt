package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.model.User
import edu.ntnu.idatt2105.gr2.backend.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class AuthenticationService(
    private val userRepository: UserRepository,
    private val authenticationManager: AuthenticationManager,
    private val passwordEncoder: PasswordEncoder,
) {
    fun signup(name: String, email: String, password: String): User {
        if (userRepository.findByEmail(email) != null) {
            throw IllegalArgumentException("User with email $email already exists")
        }

        val userToSave = User(name = name, email = email, passwordHashed = passwordEncoder.encode(password))

        return userRepository.save(userToSave)
    }

    fun authenticate(email: String, password: String): User {
        if (email.isBlank() || password.isBlank()) {
            throw IllegalArgumentException("Email and password cannot be blank")
        }

        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    email,
                    password
                )
            )
        } catch (e: AuthenticationException) {
            throw IllegalArgumentException("Invalid email or password", e)
        }

        return userRepository.findByEmail(email)
            ?: throw IllegalStateException("User $email authenticated but not found in repository.")
    }
}