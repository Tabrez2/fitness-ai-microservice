package tab.fitness.aiservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import tab.fitness.aiservice.entity.Activity;
import tab.fitness.aiservice.entity.Recommendation;
import tab.fitness.aiservice.repository.RecommendationRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {

    private final ActivityAIService activityAIService;
    private final RecommendationRepository recommendationRepository;

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 35000, multiplier = 2.0),
            include = {RuntimeException.class}
    )
    @KafkaListener(topics = "${kafka.topic.name}", groupId = "activity-processor-group")
    public void processActivity(Activity activity) {
        log.info("Received Activity for User: {}. Activity ID: {}", activity.getUserId(), activity.getId());

        // 1. IDEMPOTENCY CHECK:
        // Check if we already processed this activity. If yes, stop here.
        if (recommendationRepository.findByActivityId(activity.getId()).isPresent()) {
            log.warn("Recommendation already exists for Activity ID: {}. Skipping to prevent duplicates.", activity.getId());
            return;
        }

        try {
            // Throttling to prevent 429 errors
            Thread.sleep(4000);

            Recommendation recommendation = activityAIService.generateRecommendation(activity);

            // Double-check right before saving (optional but safer)
            if (recommendationRepository.findByActivityId(activity.getId()).isEmpty()) {
                recommendationRepository.save(recommendation);
                log.info("Successfully saved recommendation for activity: {}", activity.getId());
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted during throttling sleep");
        } catch (Exception e) {
            log.warn("AI Service failed for activity {}. Error: {}", activity.getId(), e.getMessage());
            throw e; // Triggers Kafka Retry
        }
    }

    @DltHandler
    public void handleDlt(Activity activity, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("Activity {} failed after retries. Topic: {}", activity.getId(), topic);
        // Optional: Save a default fallback here so the UI isn't stuck forever
    }
}