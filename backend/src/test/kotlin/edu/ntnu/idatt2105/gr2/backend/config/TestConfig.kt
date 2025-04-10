package edu.ntnu.idatt2105.gr2.backend.config

import edu.ntnu.idatt2105.gr2.backend.configs.JwtAuthenticationFilter
import edu.ntnu.idatt2105.gr2.backend.service.JwtService
import edu.ntnu.idatt2105.gr2.backend.service.UserContextService
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class TestConfig {
    @Bean
    @Primary
    fun testUserContextService(): UserContextService {
        val service = UserContextService()
        service.setCurrentUserId(1)
        return service
    }

    @Bean
    @Primary
    fun jwtAuthenticationFilter(): JwtAuthenticationFilter = Mockito.mock(JwtAuthenticationFilter::class.java)

    // Mocks authentication and authorization
    @Bean
    @Primary
    fun jwtService(): JwtService = Mockito.mock(JwtService::class.java)
}
