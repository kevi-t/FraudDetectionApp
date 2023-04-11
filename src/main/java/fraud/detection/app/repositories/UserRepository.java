package fraud.detection.app.repositories;

import fraud.detection.app.models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User,Long> {
   User  findUserBymobileNumber(String mobileNumber);
   User findUserByEmail(String email);
}
