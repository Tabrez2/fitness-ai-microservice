package tab.fitness.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import tab.fitness.aiservice.entity.Activity;
import tab.fitness.aiservice.entity.Recommendation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {
    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity) {
        String prompt = createPromptForActivity(activity);
        String aiResponse = geminiService.getRecommendations(prompt);
        log.info("RESPONSE FROM AI {} ", aiResponse);
        return processAIResponse(activity, aiResponse);
    }

    private Recommendation processAIResponse(Activity activity, String aiResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // 1. Robust Cleaning of Markdown Backticks
            String jsonContent = aiResponse.trim();
            if (jsonContent.contains("```")) {
                jsonContent = jsonContent.replaceAll("(?s)^.*?```json\\s*|```.*?$", "").trim();
            }

            JsonNode rootNode = mapper.readTree(jsonContent);

            // 2. Use isolated helper methods to extract lists
            // This prevents the "repeated text" bug by scoping the loop inside dedicated methods
            List<String> improvements = extractImprovements(rootNode.path("improvements"));
            List<String> suggestions = extractSuggestions(rootNode.path("suggestions"));
            List<String> safety = extractSafetyGuidelines(rootNode.path("safety"));

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .type(activity.getType().toString())
                    .recommendation(rootNode.path("analysis").path("overall").asText())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("CRITICAL PARSING ERROR: ", e);
            return createDefaultRecommendation(activity);
        }
    }

    private List<String> extractImprovements(JsonNode improvementsNode) {
        List<String> improvements = new ArrayList<>();
        if (improvementsNode != null && improvementsNode.isArray()) {
            for (JsonNode node : improvementsNode) {
                String area = node.path("area").asText().trim();
                String detail = node.path("recommendation").asText().trim();
                if (!area.isEmpty() && !detail.isEmpty()) {
                    improvements.add(area + ": " + detail);
                }
            }
        }
        return improvements.isEmpty() ?
                new ArrayList<>(Collections.singletonList("No specific improvements provided")) : improvements;
    }

    private List<String> extractSuggestions(JsonNode suggestionsNode) {
        List<String> suggestions = new ArrayList<>();
        if (suggestionsNode != null && suggestionsNode.isArray()) {
            for (JsonNode node : suggestionsNode) {
                String workout = node.path("workout").asText().trim();
                String description = node.path("description").asText().trim();
                if (!workout.isEmpty() && !description.isEmpty()) {
                    suggestions.add(workout + ": " + description);
                }
            }
        }
        return suggestions.isEmpty() ?
                new ArrayList<>(Collections.singletonList("No specific suggestions provided")) : suggestions;
    }

    private List<String> extractSafetyGuidelines(JsonNode safetyNode) {
        List<String> safety = new ArrayList<>();
        if (safetyNode != null && safetyNode.isArray()) {
            for (JsonNode node : safetyNode) {
                String text = node.asText().trim();
                if (!text.isEmpty()) {
                    safety.add(text);
                }
            }
        }
        return safety.isEmpty() ?
                new ArrayList<>(Arrays.asList("Always warm up", "Stay hydrated", "Listen to your body")) : safety;
    }

    private Recommendation createDefaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .type(activity.getType().toString())
                .recommendation("Unable to generate detailed analysis")
                .improvements(Collections.singletonList("Continue with your current routine"))
                .suggestions(Collections.singletonList("Consider consulting a fitness consultant"))
                .safety(Arrays.asList("Always warm up before exercise", "Stay hydrated", "Listen to your body"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private String createPromptForActivity(Activity activity) {
        return String.format("""
        Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
        {
          "analysis": {
            "overall": "Overall analysis here",
            "pace": "Pace analysis here",
            "heartRate": "Heart rate analysis here",
            "caloriesBurned": "Calories analysis here"
          },
          "improvements": [
            { "area": "Area name", "recommendation": "Detailed recommendation" }
          ],
          "suggestions": [
            { "workout": "Workout name", "description": "Detailed workout description" }
          ],
          "safety": [ "Safety point 1", "Safety point 2" ]
        }

        Activity: %s, Duration: %d min, Calories: %d.
        Provide unique, high-quality fitness advice. Do not repeat the same advice across different categories.
        """,
                activity.getType(), activity.getDuration(), activity.getCaloriesBurned()
        );
    }
}