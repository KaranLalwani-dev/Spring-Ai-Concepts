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

    @Test
    public void testEmbedText() {
        var embed = aiService.getEmbedding("This is a big text here");
        System.out.println(embed.length);
        for (float e : embed) {
            System.out.print(e + " ");
        }
    }

    @Test
    public void testStoreData() {
        aiService.ingestDataToVectorStore("This is a big text.");
    }

    @Test
    public void testSimilaritySearch() {
       var resposne = aiService.similaritySearch("space movie");
        System.out.println(resposne);
    }
}
