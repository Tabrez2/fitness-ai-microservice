package tab.fitness.aiservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class GeminiService {

    private final ChatClient chatClient;

    // Spring AI autoconfigures a ChatClient.Builder for you
    public GeminiService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }


    public String getRecommendations(String details) {
        log.info("Processing activity details for AI recommendation...");
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[] {
                        Map.of("parts", new Object[] {
                                Map.of("text", details)
                        })
                }
        );

        // Fluent API: No more manual JSON maps or WebClient calls!
        String response = chatClient.prompt()
                .user(details)
                .call()
                .content();

        log.info("RESPONSE FROM GEMINI: {}", response);
        return response;
    }
}