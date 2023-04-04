package fraud.detection.app.repositories;

import fraud.detection.app.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface
UserRepository extends JpaRepository <User,Long> {
   User findUserByMobileNumber(String mobileNumber);
   User findUserBymobileNumber(String mobileNumber);
}
