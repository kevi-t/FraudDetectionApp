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
public class SendMoneyDTO {
    @NotEmpty(message = "This field 'senderAccountNumber' should not be empty") //Sender
    private String senderAccountNumber;
    @NotEmpty(message = "This field 'receiverAccountNumber' should not be empty")
    private String receiverAccountNumber;
    @NotNull
    @DecimalMin(value = "1",message ="Invalid amount please try again" )
    private double transactionAmount;

}
