package tab.fitness.activityService.service;

import tab.fitness.activityService.request.ActivityRequest;
import tab.fitness.activityService.response.ActivityResponse;

import java.util.List;

public interface ActivityService {
    ActivityResponse trackActivity(ActivityRequest request);
    List<ActivityResponse> getUserActivities(String userId);
}
