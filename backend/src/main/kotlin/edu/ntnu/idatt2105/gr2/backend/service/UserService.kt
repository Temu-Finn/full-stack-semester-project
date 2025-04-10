package edu.ntnu.idatt2105.gr2.backend.service;

import edu.ntnu.idatt2105.gr2.backend.dto.ChangePasswordRequest
import edu.ntnu.idatt2105.gr2.backend.dto.UpdateEmailRequest
import edu.ntnu.idatt2105.gr2.backend.dto.UpdateNameRequest
import edu.ntnu.idatt2105.gr2.backend.dto.UserProfile
import edu.ntnu.idatt2105.gr2.backend.exception.UserAlreadyExistsException
import edu.ntnu.idatt2105.gr2.backend.exception.UserNotFoundException
import edu.ntnu.idatt2105.gr2.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import javax.transaction.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userContextService: UserContextService,
    private val passwordEncoder: BCryptPasswordEncoder
) {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    @Transactional
    fun updateEmail(request: UpdateEmailRequest): Boolean {
        val userId = userContextService.getCurrentUserId()
        val newEmail = request.email

        val existingUser = userRepository.findByEmail(newEmail)

        if (existingUser != null && existingUser.id != userId) {
            logger.warn("Email $newEmail already in use by another user")
            throw UserAlreadyExistsException("Email $newEmail already in use by another user")
        }

        logger.info("Updating email for user with ID $userId to ${request.email}")
        return userRepository.updateEmail(userId, request.email)
    }

    @Transactional
    fun updateName(request: UpdateNameRequest): Boolean {
        val userId = userContextService.getCurrentUserId()
        logger.info("Updating name for user with ID $userId to ${request.name}")
        return userRepository.updateName(userId, request.name)
    }

    @Transactional
    fun changePassword(request: ChangePasswordRequest): Boolean {
        val userId = userContextService.getCurrentUserId()
        val user = userRepository.findById(userId)
            ?: throw UserNotFoundException("User not found with ID $userId")

        if (!passwordEncoder.matches(request.currentPassword, user.password)) {
            throw IllegalArgumentException("Current password is incorrect")
        }

        val hashedNewPassword = passwordEncoder.encode(request.newPassword)
        return userRepository.updatePassword(userId, hashedNewPassword)
    }
}
