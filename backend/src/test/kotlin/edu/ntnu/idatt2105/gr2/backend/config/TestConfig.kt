package edu.ntnu.idatt2105.gr2.backend.config

import edu.ntnu.idatt2105.gr2.backend.service.UserContextService
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
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
}