package fraud.detection.app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class TransactionDTO {

    @NotEmpty(message = "This field 'accountNumber' should not be empty")
    private String accountNumber;
    @NotNull
    @DecimalMin(value = "50",message ="The minimum deposit amount is 50" )
    private Double transactionAmount;
//    @NotEmpty(message = "This field 'Type of Transaction' should not be empty")
//    private String transactionType;
//    @NotEmpty(message = "This field 'sender' should not be empty")
//    private Long userId;
//    @NotEmpty(message = "This field 'receiver' should not be empty")
//    private Long terminalId;
}
