package tab.fitness.userservices.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tab.fitness.userservices.request.UserRequest;
import tab.fitness.userservices.response.UserResponse;
import tab.fitness.userservices.service.UserService;
import tab.fitness.userservices.util.CommonUtil;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> userProfile(@PathVariable String userId) throws Exception {
        UserResponse userProfile = userService.getUserProfile(userId);
        return CommonUtil.createResponse(userProfile, HttpStatus.FOUND);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequest request){
        UserResponse userResponse = userService.registerUser(request);
        return CommonUtil.createResponse(userResponse, HttpStatus.CREATED);
    }
    @GetMapping("/{userId}/validate")
    public ResponseEntity<?> validate(@PathVariable String userId) throws Exception {
        return ResponseEntity.ok(userService.userExistsById(userId));

    }

}
