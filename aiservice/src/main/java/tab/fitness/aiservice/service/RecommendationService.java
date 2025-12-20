package tab.fitness.aiservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tab.fitness.aiservice.entity.Recommendation;
import tab.fitness.aiservice.repository.RecommendationRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;

    public List<Recommendation> getUserRecommendation(String userId) {
        return recommendationRepository.findByUserId(userId);
    }

    public Recommendation getActivityRecommendation(String activityId) {
        int maxRetries = 5;
        int delayMillis = 1000; // 1 second

        for (int i = 0; i < maxRetries; i++) {
            Optional<Recommendation> recommendation = recommendationRepository.findByActivityId(activityId);

            if (recommendation.isPresent()) {
                return recommendation.get();
            }

            log.info("Recommendation not ready for activity {}. Retrying {}/{}", activityId, i + 1, maxRetries);

            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for recommendation", e);
            }
        }

        throw new RuntimeException("No recommendation found for activity: " + activityId + " after waiting.");
    }
}