package com.karandev.learn_spring_ai.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RAGServicesTests {

    @Autowired
    private RAGService ragService;

    @Test
    public void testIngest() {
        ragService.ingestPDFToVectorStore();
    }

    @Test
    public void testAskAI() {
        String response = ragService.askAI("What is apple");
        System.out.println(response);
    }

    @Test
    public void testStoreData() {
        ragService.ingestDataToVectorStore("This is a big text.");
    }
}
