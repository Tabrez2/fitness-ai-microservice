package tab.fitness.activityService.service_impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tab.fitness.activityService.entity.Activity;
import tab.fitness.activityService.repository.ActivityRepository;
import tab.fitness.activityService.request.ActivityRequest;
import tab.fitness.activityService.response.ActivityResponse;
import tab.fitness.activityService.service.ActivityService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final ModelMapper modelMapper;
    private final UserValidationServiceImpl userValidationService;
    private final KafkaTemplate<String,Activity> kafkaTemplate;

    @Value("${kafka.topic.name}")
    private String topicName;
    public ActivityResponse trackActivity(ActivityRequest request) {

        boolean isValidUser = userValidationService.validateUser(request.getUserId());

        if(!isValidUser){
            throw new RuntimeException("Invalid user "+request.getUserId());
        }

        Activity activity = Activity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .build();

       Activity savedActivity = activityRepository.save(activity);
        try {
            kafkaTemplate.send(topicName,savedActivity.getUserId(),savedActivity);
        } catch (Exception e) {
            e.printStackTrace();
        }


        ActivityResponse activityResponse = modelMapper.map(savedActivity, ActivityResponse.class);

        return activityResponse;
    }

    public List<ActivityResponse> getUserActivities(String userId) {
        List<Activity> activityList = activityRepository.findByUserId(userId);
        return activityList.stream()
                .map(activity -> modelMapper.map(activity, ActivityResponse.class))
                .collect(Collectors.toList());


    }
}
