package fraud.detection.app.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class StatementDTO {

    @NotEmpty(message = "This field 'accountNumber' should not be empty")
    private String accountNumber;
    @NotEmpty(message = "Enter the pin")
    private String pin;
}
