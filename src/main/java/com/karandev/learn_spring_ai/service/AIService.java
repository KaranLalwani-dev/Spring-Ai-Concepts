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
import java.util.stream.Collectors;

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


}
