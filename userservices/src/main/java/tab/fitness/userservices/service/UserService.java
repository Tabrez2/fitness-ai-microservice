package tab.fitness.userservices.service;

import tab.fitness.userservices.exception.ResourceNotFoundException;
import tab.fitness.userservices.request.UserRequest;
import tab.fitness.userservices.response.UserResponse;

public interface UserService {
    UserResponse getUserProfile(String userId) throws ResourceNotFoundException, Exception;
    UserResponse registerUser(UserRequest request);

    Boolean userExistsById(String userId);
}
