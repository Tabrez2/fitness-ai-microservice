package tab.fitness.userservices.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import tab.fitness.userservices.exception.ResourceExistsException;
import tab.fitness.userservices.repository.UserRepository;
import tab.fitness.userservices.request.UserRequest;

@Component
@RequiredArgsConstructor
public class Validation {
    private final UserRepository userRepository;
    public  void userValidation(UserRequest userRequest) throws Exception {

        if(!StringUtils.hasText(userRequest.getFirstName())){
            throw new IllegalArgumentException("first name is invalid");
        }

        if(!StringUtils.hasText(userRequest.getLastName())){
            throw new IllegalArgumentException("last name is invalid");
        }

        if(!StringUtils.hasText(userRequest.getEmail())){
            throw new IllegalArgumentException("email is invalid");
        }
        else{
            boolean emailExist = userRepository.existsByEmail(userRequest.getEmail());
            if(emailExist){
                throw new ResourceExistsException("email already exists");
            }
        }

        if(!StringUtils.hasText(userRequest.getPassword())){
            throw new IllegalArgumentException("password is invalid");
        }

    }
}
