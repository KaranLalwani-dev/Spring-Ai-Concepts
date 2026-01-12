package com.karandev.learn_spring_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RAGService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final ChatMemory chatMemory;

    @Value("classpath:lec8.pdf")
    Resource pdfFile;

    public String askAIWithAdvisors(String prompt, String userId) {
        return chatClient.prompt()
                .system("""
                        You are an AI assistant Cody greet users with your name (Cody) and the user name if your know their name.
                        Answer in a friendly and conversational tone.
                        """)
                .user(prompt)
                .advisors( // we have both short term memory and long term memory.

                        MessageChatMemoryAdvisor.builder(chatMemory) // first this short term memory advisor will work, so this basically considers the last 10 or 20 messages of teh chat and not too long hence the name short term.
                                .conversationId(userId)// after passing through this short term advisor we will have an enhanced prompt which will be passed on to the long term memory advisor
                                .build(),

                        VectorStoreChatMemoryAdvisor.builder(vectorStore) // vector store will store the chat history and the knowledge base in the same table only.
                                .conversationId(userId) // but since we have the userId with the conversation id hence we can differentiate between the chat history of multiple users and the knowledge base.
                                .defaultTopK(4)// With this type of advisor we will have a long term memory because teh conversations are getting stored in the postgres vector db hence if a user asks something about what he talked 10 days ago we will be able to answer that.
                                .build(),

                        // With this is the model will answer based only on the pdf that is provided and if it cannot find the relevant information then it
                        // will just say that it does not know the answer.
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder()
                                        .filterExpression("file_name == lec8.pdf")
                                        .topK(4)
                                        .build())
                                .build()
                )
                .call()
                .content();
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
                        
                        Final Answer:
                        """;

        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder()
                .query(prompt)
                .topK(2)
                .similarityThreshold(0.5)
                .filterExpression("file_name == 'lec8'")
                .build());

        String context = documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        PromptTemplate promptTemplate = new PromptTemplate(template);
        String systemPrompt = promptTemplate.render(Map.of("context", context));

        // Using advisors we can augment our prompt better.
        return chatClient.prompt()
                .system(systemPrompt)
                .user(prompt)
                .call()
                .content();
    }

    public void ingestPDFToVectorStore() {
        PagePdfDocumentReader reader = new PagePdfDocumentReader(pdfFile);
        List<Document> pages = reader.get(); // we now have a list of documents.
        TokenTextSplitter tokenTextSplitter = TokenTextSplitter.builder()
                .withChunkSize(200)
                .build();

        List<Document> chunks = tokenTextSplitter.apply(pages);
        vectorStore.add(chunks);
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
}
