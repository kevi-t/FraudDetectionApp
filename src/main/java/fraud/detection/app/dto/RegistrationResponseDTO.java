package fraud.detection.app.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class RegistrationResponseDTO {
    private String phoneNumber;
}
