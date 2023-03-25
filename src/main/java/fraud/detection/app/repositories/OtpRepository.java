package fraud.detection.app.repositories;

import fraud.detection.app.models.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepository extends JpaRepository<Otp,Long> {
    Otp findOtpByMobileNumber(String mobileNumber);
    String deleteByMobileNumber(String mobileNumber);
}
