package fraud.detection.app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class DepositDTO {
    @NotEmpty(message = "This field 'accountNumber' should not be empty")
    private String accountNumber;
    @NotNull
    // Prevent fraudulent transfers attempting to abuse currency conversion errors
    @Positive(message = "Transfer amount must be positive")
    @DecimalMin(value = "50",message ="The minimum deposit amount is 50" )
    private Double transactionAmount;
    private String pin;

}
