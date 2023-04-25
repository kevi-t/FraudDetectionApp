package fraud.detection.app.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class LoginResponseDTO {
    private String userPhoneNumber;
    private String token;
    private double accountBalance;

}
