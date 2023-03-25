package fraud.detection.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class VerifyOtpDTO {
    private String phoneNumber;
    private int otp;
}
