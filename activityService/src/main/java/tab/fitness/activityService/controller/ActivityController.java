package tab.fitness.activityService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tab.fitness.activityService.request.ActivityRequest;
import tab.fitness.activityService.response.ActivityResponse;
import tab.fitness.activityService.service.ActivityService;
import tab.fitness.activityService.util.CommonUtil;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/activities")
public class    ActivityController {

    private final ActivityService activityService;

    @PostMapping
    public ResponseEntity<?> trackActivity(@RequestBody ActivityRequest request, @RequestHeader("X-User-ID") String userId) {
        request.setUserId(userId);
        ActivityResponse activityResponse = activityService.trackActivity(request);
        return CommonUtil.createResponse(activityResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ActivityResponse>> getUserActivities(@RequestHeader("X-User-ID") String userId) {
        return ResponseEntity.ok(activityService.getUserActivities(userId));
    }

}