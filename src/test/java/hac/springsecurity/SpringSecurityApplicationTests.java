package hac.springsecurity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test. {@code @SpringBootTest} loads the FULL application context exactly as the real
 * application does. If any bean is misconfigured (including the security configuration), the
 * context fails to start and this test fails — so even an empty test body is meaningful.
 */
@SpringBootTest
class SpringSecurityApplicationTests {

    @Test
    void contextLoads() {
        // Intentionally empty: success simply means the Spring context started without errors.
    }

}
