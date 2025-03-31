package edu.ntnu.idatt2105.gr2.backend.service

import org.springframework.stereotype.Service
import org.springframework.web.context.annotation.RequestScope

@Service
@RequestScope
class UserContextService {
    private var userId: Int? = null

    fun getCurrentUserId(): Int {
        return userId ?: throw IllegalStateException("No userId set in current context")
    }

    fun setCurrentUserId(userId: Int) {
        this.userId = userId
    }
}