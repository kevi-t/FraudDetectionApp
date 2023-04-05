package fraud.detection.app.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class AuthenticationDTO {

    @NotEmpty(message = "This field 'mobileNumber' should not be empty")
    private  String mobileNumber;
    @NotEmpty(message = "This field 'Password' should not be empty")
    private String password;
}
