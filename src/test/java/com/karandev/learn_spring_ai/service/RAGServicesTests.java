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
        String response = ragService.askAI("What is apple");// when we have added the pdf document now the model won't be able to answer anything that has no similarity with the pdf.
        System.out.println(response);
    }

    @Test
    public void testStoreData() {
        ragService.ingestDataToVectorStore("This is a big text.");
    }

    @Test
    public void testAskAIWithAdvisors() {
        String response = ragService.askAIWithAdvisors("What is teh capital of India also my name is karan?", "Karan123");
        System.out.println(response);
    }
    // After running thsi we can ask it to tell our name and it will be able to tell it.

}
