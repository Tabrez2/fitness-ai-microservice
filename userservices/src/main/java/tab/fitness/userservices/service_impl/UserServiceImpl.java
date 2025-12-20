package tab.fitness.userservices.service_impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import tab.fitness.userservices.entity.Role;
import tab.fitness.userservices.entity.User;
import tab.fitness.userservices.exception.ResourceNotFoundException;
import tab.fitness.userservices.repository.UserRepository;
import tab.fitness.userservices.request.UserRequest;
import tab.fitness.userservices.response.UserResponse;
import tab.fitness.userservices.service.UserService;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

   private final UserRepository userRepository;
   private final ModelMapper modelMapper;

    @Override
    public UserResponse getUserProfile(String userId) throws Exception {
        User user =userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User not found"));
        UserResponse userResponse = modelMapper.map(user, UserResponse.class);
        return userResponse;
    }

    @Override
    public UserResponse registerUser(UserRequest request) {

        if(userRepository.existsByEmail(request.getEmail())) {

            User existingUser = userRepository.findByEmail(request.getEmail());

            UserResponse userResponse = modelMapper.map(existingUser, UserResponse.class);
            return userResponse;
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .keycloakId(request.getKeycloakId())
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        UserResponse userResponse = modelMapper.map(savedUser, UserResponse.class);
        return userResponse;

    }

    @Override
    public Boolean userExistsById(String userId) {
       return userRepository.existsByKeycloakId(userId);
    }
}
