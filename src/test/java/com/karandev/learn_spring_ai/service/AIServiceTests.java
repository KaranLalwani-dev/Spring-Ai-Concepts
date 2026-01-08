package com.karandev.learn_spring_ai.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AIServiceTests {

    @Autowired
    private AIService aiService;

    @Test
    public void getJoke() {
        var joke = aiService.getJoke("Dogs");
        System.out.println(joke);
    }
}
