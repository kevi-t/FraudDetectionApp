package fraud.detection.app.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class OtpDTO {
    private int otp;
}
