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
public class CheckBalanceDTO {

    @NotEmpty(message = "This field 'accountNumber' should not be empty")
    private String accountNumber;
    private String pin;
}
