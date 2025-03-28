package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.model.User
import edu.ntnu.idatt2105.gr2.backend.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service


@Service
class AuthenticationService(
    private val userRepository: UserRepository,
    private val authenticationManager: AuthenticationManager,
    private val passwordEncoder: BCryptPasswordEncoder,
) {
    fun signup(username: String, email: String, password: String): User {
        // Input is already validated with jakarta.validation
        // Check if user already exists
        if (userRepository.existsByUsername(username)) {
            throw IllegalArgumentException("User with username $username already exists")
        }

        val user = User(username, email, passwordEncoder.encode(password))

        return userRepository.save(user)
    }

    fun authenticate(username: String, password: String): User? {
        if (username.isBlank() || password.isBlank()) {
            throw IllegalArgumentException("Username and password cannot be blank")
        }

        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                username,
                password
            )
        )

        return userRepository.findByUsername(username)
    }
}