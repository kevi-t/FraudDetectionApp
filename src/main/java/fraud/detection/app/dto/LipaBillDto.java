package fraud.detection.app.dto;
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
public class LipaBillDto {

    private String pin;
    private String payerNo;
    private String payBillNo;
    private double amount;

}
