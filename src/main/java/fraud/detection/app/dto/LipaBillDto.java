package fraud.detection.app.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
@Data
public class LipaBillDto {
    @NotEmpty(message = "This field 'pin' should not be empty")
    private String pin;
    @NotEmpty(message = "This field 'payerno' should not be empty")
    private String PayerNo;
    @NotEmpty(message = "This field 'paybill' should not be empty")
    private String payBillNo;
    @NotEmpty(message = "This field 'amount' should not be empty")
    private double amount;

}
