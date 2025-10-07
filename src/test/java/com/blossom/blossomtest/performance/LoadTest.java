package com.blossom.blossomtest.performance;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@AutoConfigureMockMvc
class LoadTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testPerformanceOnLogin() throws Exception {
        int requests = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < requests; i++) {
            executor.submit(() -> {
                try {
                    mockMvc.perform(post("/users/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\": \"miguel@example.com\", \"password\": \"1234\"}"))
                            .andExpect(status().isOk());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
    }
}
