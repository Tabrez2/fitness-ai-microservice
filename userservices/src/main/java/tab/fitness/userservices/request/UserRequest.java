package tab.fitness.userservices.request;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequest {

    private String email;
    private String keycloakId;
    private String password;
    private String firstName;
    private String lastName;
}
