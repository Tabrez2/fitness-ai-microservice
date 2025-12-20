package tab.fitness.userservices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tab.fitness.userservices.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);

    Boolean existsByKeycloakId(String userId);

    User findByEmail(String email);
}
