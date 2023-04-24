package fraud.detection.app.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class LoginResponse {
   private String token;
    private String userPhoneNumber;
    private  String userEmail;

}
