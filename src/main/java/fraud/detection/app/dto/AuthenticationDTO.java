package fraud.detection.app.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Validated
public class AuthenticationDTO {

    @NotEmpty(message = "This field 'mobileNumber' should not be empty")
    private  String mobileNumber;
    @NotEmpty(message = "This field 'Pin' should not be empty")
    private String pin;
}
