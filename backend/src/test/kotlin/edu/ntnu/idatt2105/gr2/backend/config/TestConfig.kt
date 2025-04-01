package edu.ntnu.idatt2105.gr2.backend.config

import edu.ntnu.idatt2105.gr2.backend.configs.JwtAuthenticationFilter
import edu.ntnu.idatt2105.gr2.backend.model.User
import edu.ntnu.idatt2105.gr2.backend.service.UserContextService
import edu.ntnu.idatt2105.gr2.backend.service.JwtService
import edu.ntnu.idatt2105.gr2.backend.service.AuthenticationService
import org.mockito.Mockito
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import javax.sql.DataSource

@TestConfiguration
class TestConfig {
    @Bean
    fun dataSource(): DataSource {
        return EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .build()
    }

    @Bean
    @Primary
    fun testUserContextService(): UserContextService {
        val service = UserContextService()
        service.setCurrentUserId(1)
        return service
    }

    @Bean
    @Primary
    fun jwtAuthenticationFilter(): JwtAuthenticationFilter {
        return Mockito.mock(JwtAuthenticationFilter::class.java)
    }

    // Mocks authentication and authorization
    @Bean
    @Primary
    fun jwtService(): JwtService {
        return Mockito.mock(JwtService::class.java)
    }
}