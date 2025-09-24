package com.example.franchise_api;

import com.example.franchise.FranchiseApiApplication; // updated to reference the single canonical application class
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.franchise.testsupport.TestMetricsConfig;
import org.springframework.context.annotation.Import;

@SpringBootTest(classes = FranchiseApiApplication.class)
@Import(TestMetricsConfig.class)
class FranchiseApiApplicationTests {

    @Test
    void contextLoads() {
    }
}
