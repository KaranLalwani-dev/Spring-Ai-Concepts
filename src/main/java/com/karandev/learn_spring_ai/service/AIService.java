package com.karandev.learn_spring_ai.service;

import com.karandev.learn_spring_ai.dto.Joke;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIService {

    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;

    public float[] getEmbedding(String text) {
        return embeddingModel.embed(text);
    }

    public List<Document> similaritySearch(String text) {
        return vectorStore.similaritySearch(SearchRequest.builder()
                        .query(text)
                        .topK(3)
                        .similarityThreshold(0.3)
                .build());
    }

    public void ingestDataToVectorStore(String text) {
        List<Document> movieDocuments = List.of(

                new Document(
                        "A skilled thief enters people's dreams to plant an idea for corporate espionage.",
                        Map.of(
                                "movieId", "MOV_001",
                                "title", "Inception",
                                "year", 2010,
                                "director", "Christopher Nolan",
                                "genres", List.of("Sci-Fi", "Thriller"),
                                "language", "English",
                                "rating", 8.8
                        )
                ),

                new Document(
                        "Astronauts travel through a wormhole to find a new home for humanity as Earth becomes uninhabitable.",
                        Map.of(
                                "movieId", "MOV_002",
                                "title", "Interstellar",
                                "year", 2014,
                                "director", "Christopher Nolan",
                                "genres", List.of("Sci-Fi", "Drama"),
                                "language", "English",
                                "rating", 8.6
                        )
                ),

                new Document(
                        "Batman faces a chaotic criminal mastermind who seeks to destroy Gotham’s moral order.",
                        Map.of(
                                "movieId", "MOV_003",
                                "title", "The Dark Knight",
                                "year", 2008,
                                "director", "Christopher Nolan",
                                "genres", List.of("Action", "Crime", "Drama"),
                                "language", "English",
                                "rating", 9.0
                        )
                )
        );

        vectorStore.add(movieDocuments);
        vectorStore.add(getSpringAI());
    }

    public String getJoke(String topic) {
        String systemPrompt = """
            You are a sarcastic joker, you make poetic jokes in 4 lines
            You don't make jokes about politics.
            Give a joke on the topic : {topic}
            """;
        PromptTemplate promptTemplate = new PromptTemplate(systemPrompt);
        String renderedTest = promptTemplate.render(Map.of("topic", topic));

/*        With the help of advisor we can intercept our requests, we can do some things with the prompt(modify it).
          that way the modified prompt will be sent to the llm;
          and we can also do better debugging.

          One more important aspect of spring AI is parsing that we can parse our response to any kind of object that we want
          and springAI will help us to convert our prompt here, in such a way that we will get a nice json which is compatible
          with any kind of object that we pass here.

          for example, we don't want to get a string joke we want to get a DTO here.
          also we would have to do manual parsing here if we were using the REST API instead of springAI.
*/        var response = chatClient.prompt()
                .user(renderedTest)
                .advisors(
                        new SimpleLoggerAdvisor()
                )
                .call()
                .entity(Joke.class);

        return response.text(); // this will just get the text here , but we can get the whole joke object here.
    }

    public String askAI(String prompt) {

        String template = """
                        You are a highly knowledgeable technical assistant specializing in Spring AI.
                        
                        Your task is to answer the user's question using ONLY the information provided in the context.
                        The context consists of retrieved documents from a vector store and may contain partial,
                        overlapping, or loosely related information.
                        
                        Instructions:
                        - Carefully read and analyze the entire context before answering.
                        - Extract only the information that is directly relevant to the user's question.
                        - Do NOT introduce external knowledge, assumptions, or hallucinations.
                        - If the context does not contain sufficient information to answer the question,
                          clearly state that the answer cannot be determined from the provided context.
                        - Keep the response clear, precise, and technically accurate.
                        - Prefer concise explanations, but include technical detail when necessary for correctness.
                        
                        Context:
                        {context}
                        
                        User Question:
                        {question}
                        
                        Final Answer:
                        """;


        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    public static List<Document> getSpringAI() {
        List<Document> springAiDocuments = List.of(

                new Document(
                        "Spring AI provides a unified abstraction layer for integrating large language models into Spring applications.",
                        Map.of(
                                "docId", "SPRING_AI_001",
                                "topic", "Overview",
                                "framework", "Spring AI",
                                "category", "Introduction",
                                "language", "Java"
                        )
                ),

                new Document(
                        "Spring AI supports multiple model providers such as OpenAI, Azure OpenAI, and local models through a common API.",
                        Map.of(
                                "docId", "SPRING_AI_002",
                                "topic", "Model Providers",
                                "framework", "Spring AI",
                                "category", "Configuration",
                                "language", "Java"
                        )
                ),

                new Document(
                        "Spring AI enables prompt templates to standardize and reuse prompts across different AI model calls.",
                        Map.of(
                                "docId", "SPRING_AI_003",
                                "topic", "Prompt Templates",
                                "framework", "Spring AI",
                                "category", "Prompt Engineering",
                                "language", "Java"
                        )
                ),

                new Document(
                        "Spring AI integrates vector stores to support semantic search and retrieval-augmented generation workflows.",
                        Map.of(
                                "docId", "SPRING_AI_004",
                                "topic", "Vector Stores",
                                "framework", "Spring AI",
                                "category", "RAG",
                                "language", "Java"
                        )
                ),

                new Document(
                        "Spring AI provides chat and embedding clients that follow familiar Spring programming models.",
                        Map.of(
                                "docId", "SPRING_AI_005",
                                "topic", "Chat and Embeddings",
                                "framework", "Spring AI",
                                "category", "Core API",
                                "language", "Java"
                        )
                )
        );

        return springAiDocuments;
    }

}
