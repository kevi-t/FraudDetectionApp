package fraud.detection.app.dto;

import lombok.Data;

@Data
public class ResetPasswordDTO {
    private String pin;
    private String phoneNumber;
}
