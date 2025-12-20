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
            backoff = @Backoff(delay = 35000, multiplier = 2.0), // Wait 35s on first fail, then 70s
            include = {RuntimeException.class} // Catch the "Failed to generate content" error
    )
    @KafkaListener(topics = "${kafka.topic.name}", groupId = "activity-processor-group")
    public void processActivity(Activity activity) {
        log.info("Processing Activity for User: {}. Activity ID: {}", activity.getUserId(), activity.getId());

        try {
            // CRITICAL: Force a small delay BEFORE calling AI.
            // This prevents the "burst" that causes the 429 error.
            Thread.sleep(4000);

            Recommendation recommendation = activityAIService.generateRecommendation(activity);
            recommendationRepository.save(recommendation);

            log.info("Successfully saved recommendation for activity: {}", activity.getId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted during throttling sleep");
        } catch (Exception e) {
            log.warn("AI Service failed for activity {}. Moving to retry topic. Error: {}",
                    activity.getId(), e.getMessage());
            // Throw exception to trigger @RetryableTopic
            throw e;
        }
    }

    @DltHandler
    public void handleDlt(Activity activity, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("Event from topic {} failed after all retries. User: {}", topic, activity.getUserId());
        // Here you could save a "default" recommendation so the user doesn't see a blank screen
    }
}