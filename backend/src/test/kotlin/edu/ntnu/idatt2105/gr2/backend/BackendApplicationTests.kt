package edu.ntnu.idatt2105.gr2.backend

import edu.ntnu.idatt2105.gr2.backend.config.TestConfig
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(TestConfig::class)
class BackendApplicationTests {
    @Test
    fun contextLoads() {
    }
}
