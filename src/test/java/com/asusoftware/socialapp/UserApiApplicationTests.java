package com.asusoftware.socialapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:postgresql://localhost:5432/socialize",
        "spring.datasource.username=postgres",
        "spring.datasource.password=password"
})
class UserApiApplicationTests {

    @Test
    void contextLoads() {
    }

}
