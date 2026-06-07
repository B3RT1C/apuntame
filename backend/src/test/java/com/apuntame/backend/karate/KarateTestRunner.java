package com.apuntame.backend.karate;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KarateTestRunner {

    @Test
    void runAll() {
        Results results = Runner.path("classpath:karate").parallel(1);
        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }
}
